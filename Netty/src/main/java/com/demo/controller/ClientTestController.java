package com.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.utils.Constants;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("")
public class ClientTestController {

	private static final Logger logger = LoggerFactory.getLogger(ClientTestController.class);
	
	@GetMapping(value = "/test/netty")
	public void testNettyConnection() {
		this.start(8090, 100);
	}

	public void start(final int beginPort, int nPort) {
		logger.info("client starting....");
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		final Bootstrap bootstrap = new Bootstrap();

		bootstrap.group(eventLoopGroup);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.SO_REUSEADDR, true);

		bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) {
			}
		});

		int index = 0;
		int port;

		while (!Thread.interrupted()) {
			port = beginPort + index;
			try {
				ChannelFuture channelFuture = bootstrap.connect(Constants.SERVER_HOST, port);
				channelFuture.addListener((ChannelFutureListener) future -> {
					if (!future.isSuccess()) {
						logger.error("Conect fail...");
						System.exit(0);
					}
				});
				channelFuture.get();
			} catch (Exception e) {
			}
			if (++index == nPort) {
				index = 0;
			}
		}
	}
}
