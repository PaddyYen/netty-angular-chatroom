package com.demo.handler;

import java.time.LocalDate;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.demo.utils.Constants;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

@Component
@Qualifier("textWebSocketFrameHandler")
@ChannelHandler.Sharable
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<Object> {

	private static final Logger logger = LoggerFactory.getLogger(TextWebSocketFrameHandler.class);

	private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private static AttributeKey<WebSocketServerHandshaker> ATTR_HANDSHAKER = AttributeKey
			.newInstance("ATTR_KEY_CHANNELID");
	private WebSocketServerHandshaker handshaker;
	public AtomicInteger nConnection = new AtomicInteger();

	public TextWebSocketFrameHandler() {
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
			logger.info("【在線人數】:" + nConnection.get() + "人");
		}, 0, 2, TimeUnit.SECONDS);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		logger.info("receive msg:" + msg.toString());
		if (msg instanceof FullHttpRequest) {
			handleHttpRequest(ctx, (FullHttpRequest) msg);
		} else if (msg instanceof WebSocketFrame) {
			handleWebSocketFrame(ctx, (WebSocketFrame) msg);
		}
	}

	private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
		// 該請求若不是websocket upgrade
		if (!req.decoderResult().isSuccess()
				|| !(Constants.WEBSOCKET_CONNECTION.equals(req.headers().get(Constants.WEBSOCKET_UPGRADE)))) {
			sendHttpResponse(ctx, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK));
			return;
		}
		WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(Constants.WEBSOCKET_URI_ROOT,
				null, false);
		handshaker = factory.newHandshaker(req);
		if (handshaker == null) {
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
		} else {
			handshaker.handshake(ctx.channel(), req);
		}
		return;
	}

	private void sendHttpResponse(ChannelHandlerContext ctx, DefaultFullHttpResponse res) {
		if (res.status().code() != 200) {
			ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
			res.content().writeBytes(buf);
			buf.release();
		}
		ChannelFuture f = ctx.channel().writeAndFlush(res);
		if (res.status().code() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
		// text frame
		if (frame instanceof TextWebSocketFrame) {
			String text = ((TextWebSocketFrame) frame).text();
			logger.info("recieve TextWebSocketFrame from channel {}", ctx.channel());
			channels.writeAndFlush(new TextWebSocketFrame("【" + LocalDate.now() + " 】:" + text));
			return;
		}
		// ping frame
		if (frame instanceof PingWebSocketFrame) {
			logger.info("recieve PingWebSocketFrame from channel {}", ctx.channel());
			ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
			return;
		}
		if (frame instanceof PongWebSocketFrame) {
			logger.info("recieve PongWebSocketFrame from channel {}", ctx.channel());
			return;
		}
		// close frame,
		if (frame instanceof CloseWebSocketFrame) {
			logger.info("recieve CloseWebSocketFrame from channel {}", ctx.channel());
			WebSocketServerHandshaker handshaker = ctx.channel().attr(ATTR_HANDSHAKER).get();
			if (handshaker == null) {
				logger.error("channel {} have no HandShaker", ctx.channel());
				return;
			}
			handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
			return;
		}
		// 剩下的是binary frame, 忽略
		logger.warn("unhandle binary frame from channel {}", ctx.channel());
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		channels.add(ctx.channel());
//		channels.writeAndFlush(new TextWebSocketFrame("[新用户] - " + ctx.channel().id() + " 加入"));
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		channels.remove(ctx.channel());
//		channels.writeAndFlush(new TextWebSocketFrame("[用户] - " +  ctx.channel().id() + " 離開"));
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.info("用戶:" + ctx.channel().id() + "異常");
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		nConnection.incrementAndGet();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		nConnection.decrementAndGet();
	}

}
