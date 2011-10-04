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

public class ClientEndpoint<S, C> implements IClientEndpoint<S, C> {
	private final ChannelUpstreamHandler channelHandler;
	private final ClientRegistrationWrapper<C> clientRegistrations;
	private volatile Channel channel;
	private volatile S serverMethods;
	
	public ClientEndpoint(ChannelUpstreamHandler channelHandler, ClientRegistrationWrapper<C> clientRegistrations) {
		this.channelHandler = channelHandler;
		this.clientRegistrations = clientRegistrations;
	}
	
	public Channel getChannel() {
		return channel;
	}
	
	protected void setServerMethods(S serverMethods) {
		this.serverMethods = serverMethods;
	}
	
	@Override
	public S getServerMethods() {
		return serverMethods;
	}

	public void connect(String host, int port, final CompletionCallback<Void> cb) {
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
	public void registerClient(C client) {
		clientRegistrations.add(client);
	}
	
	@Override
	public boolean shutdown() {
		return false;
	}
}
