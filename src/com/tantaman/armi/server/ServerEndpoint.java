package com.tantaman.armi.server;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

public class ServerEndpoint<T> implements IServerEndpoint<T> {
	private final ServerChannelHandler channelHandler;
	private volatile Channel channel;
	private volatile T clientMethods;
	
	public ServerEndpoint(ServerChannelHandler channelHandler) {
		this.channelHandler = channelHandler;
	}
	
	@Override
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
						channelHandler);
			}
		});

		// Bind and start to accept incoming connections.
		channel = bootstrap.bind(new InetSocketAddress((String)host, (Integer)port));
	}

	protected void setClientMethods(T clientMethods) {
		this.clientMethods = clientMethods;
	}
	
	public T getClientMethods() {
		return clientMethods;
	}
	
	@Override
	public void shutdown() {
		channel.close();
	}
	
	public Channel getChannel() {
		return channel;
	}
	
	public Set<Channel> getChannels() {
		return channelHandler.getChannels();
	}
}
