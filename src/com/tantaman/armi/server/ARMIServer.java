package com.tantaman.armi.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;


public class ARMIServer {
	private final Object delegate;

	public ARMIServer(Object delegate) {
		this.delegate = delegate;
	}

	public void listen(String host, int port) {
		ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		// Set up the pipeline factory.
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(
						new ObjectEncoder(),
						new ObjectDecoder(),
						new ServerChannelHandler(delegate));
			}
		});

		// Bind and start to accept incoming connections.
		bootstrap.bind(new InetSocketAddress(host, port));
	}

	// TODO: allow a client interface to be passed
	// so server can broadcast something to all clients.
	// Return an instance of the client interface and endpoint?
	public static ARMIServer create(Object delegate) {
		return new ARMIServer(delegate);
	}
}
