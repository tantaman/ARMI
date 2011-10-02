package com.tantaman.armi;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

import com.tantaman.commons.lang.ObjectUtils;


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
						new ChannelHandler());
			}
		});

		// Bind and start to accept incoming connections.
		bootstrap.bind(new InetSocketAddress(host, port));
	}

	public static ARMIServer create(Object delegate) {
		return new ARMIServer(delegate);
	}
	
	private class ChannelHandler extends SimpleChannelUpstreamHandler {
		@Override
		public void messageReceived(final ChannelHandlerContext ctx, MessageEvent e)
				throws Exception {
			System.out.println("GOT A MESSAGE DAWG!");
			final Request req = (Request)e.getMessage();
			 
			// TODO pass a CB to the delegate so we can be notified when it is done.
			Class<?> [] types = ObjectUtils.getTypes(req.getArgs(), CompletionCallback.class);
			
			Object [] args = new Object[req.getArgs().length + 1];
			System.arraycopy(req.getArgs(), 0, args, 0, req.getArgs().length);
			args[req.getArgs().length] = new CompletionCallback<Object>() {
				@Override
				public void operationCompleted(Object retVal) {
					if (req.expectsResponse()) {
						Return retMsg = new Return(req.getId(), retVal);
						ctx.getChannel().write(retMsg);
					}
				}
			};
			
			System.out.println(Arrays.toString(types));
			delegate.getClass()
			.getMethod(req.getMethod(), types)
			.invoke(delegate, args);
		}
	}
}
