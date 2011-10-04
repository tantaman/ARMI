package com.tantaman.armi.client;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

import com.tantaman.armi.CompletionCallback;

public class ClientEndpoint<T> implements IClientEndpoint<T> {
	private final ChannelUpstreamHandler channelHandler;
	private final ClientRegistrationWrapper<T> clientRegistrations;
	private volatile Channel channel;
	
	public ClientEndpoint(ChannelUpstreamHandler channelHandler, ClientRegistrationWrapper<T> clientRegistrations) {
		this.channelHandler = channelHandler;
		this.clientRegistrations = clientRegistrations;
	}
	
	public Channel getChannel() {
		return channel;
	}

	public void ARMIconnect(String host, int port, final CompletionCallback<Void> cb) {
		String sHost = (String)host;
		int iPort = (Integer)port;

		ClientBootstrap bootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(
						new ObjectEncoder(),
						new ObjectDecoder(),
						channelHandler);
			}
		});

		// Start the connection attempt.
		ChannelFuture f = bootstrap.connect(new InetSocketAddress(sHost, iPort));

		f.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				channel = future.getChannel();
			}
		});

		f.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				((CompletionCallback<Void>)cb).operationCompleted(null);
			}
		});
	}
	
	@Override
	public void ARMIregisterClient(T client) {
		clientRegistrations.add(client);
	}
	
	@Override
	public boolean ARMIshutdown() {
		return false;
	}
}
