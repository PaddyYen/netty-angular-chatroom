package com.demo.config;

import java.net.InetSocketAddress;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.demo.utils.Constants;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

@Component
public class TCPServer {
	
	@Autowired
	@Qualifier("serverBootstrap")
	private ServerBootstrap serverBootstrap;

	@Autowired
	@Qualifier("tcpSocketAddress")
	private InetSocketAddress tcpPort;

	private Channel serverChannel;
	private static final Logger logger = LoggerFactory.getLogger(TCPServer.class);

	public void start() throws Exception {
		for (int i = 0; i < Constants.N_PORT; i++) {
			int port = Constants.BEGIN_PORT + i;
			serverBootstrap.bind(port).addListener((ChannelFutureListener) future -> {
				logger.info("bind success in port: " + port);
			});
		}
//		serverChannel = serverBootstrap.bind(tcpPort).sync().channel().closeFuture().sync().channel();
	}

	@PreDestroy
	public void stop() throws Exception {
		serverChannel.close();
		serverChannel.parent().close();
	}

	public ServerBootstrap getServerBootstrap() {
		return serverBootstrap;
	}

	public void setServerBootstrap(ServerBootstrap serverBootstrap) {
		this.serverBootstrap = serverBootstrap;
	}

	public InetSocketAddress getTcpPort() {
		return tcpPort;
	}

	public void setTcpPort(InetSocketAddress tcpPort) {
		this.tcpPort = tcpPort;
	}
}
