package com.demo.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.demo.dao.RedisDao;
import com.demo.entity.RandomName;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

@Component
@Qualifier("textWebSocketFrameHandler")
@ChannelHandler.Sharable
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

	public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	@Autowired
	private RedisDao redisDao;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
		Channel incoming = ctx.channel();
		String uName = redisDao.getString(incoming.id() + "");
		for (Channel channel : channels) {
			if (channel != incoming) {
				channel.writeAndFlush(new TextWebSocketFrame("[" + uName + "] - " + msg.text()));
			} else {
				channel.writeAndFlush(new TextWebSocketFrame("[you] - " + msg.text()));
			}
		}
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		String uName = new RandomName().getRandomName();

		Channel incoming = ctx.channel();
		for (Channel channel : channels) {
			channel.writeAndFlush(new TextWebSocketFrame("[新用户] - " + uName + " 加入"));
		}
		redisDao.saveString(incoming.id() + "", uName);
		channels.add(ctx.channel());
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();
		String uName = redisDao.getString(String.valueOf(incoming.id()));
		for (Channel channel : channels) {
			channel.writeAndFlush(new TextWebSocketFrame("[用户] - " + uName + " 離開"));
		}
		redisDao.deleteString(String.valueOf(incoming.id()));

		channels.remove(ctx.channel());
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();
		System.out.println("用戶:" + redisDao.getString(incoming.id() + "") + "上線");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();
		System.out.println("用戶:" + redisDao.getString(incoming.id() + "") + "下線");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		Channel incoming = ctx.channel();
		System.out.println("用戶:" + redisDao.getString(incoming.id() + "") + "異常");
		cause.printStackTrace();
		ctx.close();
	}
}
