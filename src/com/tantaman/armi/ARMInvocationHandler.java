package com.tantaman.armi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

import com.tantaman.armi.client.ClientEndpoint;
import com.tantaman.armi.server.ServerEndpoint;
import com.tantaman.commons.lang.ObjectUtils;

// TODO: add ability to register a client
// so it can be called from the server
public class ARMInvocationHandler implements InvocationHandler {
	private final Map<Long, CompletionCallback<Object>> callbacks;
	private final AtomicLong currRequestID;
	private final ChannelUpstreamHandler channelHandler;
	private volatile Channel channel;
	
	public ARMInvocationHandler(Object delegate) {
		callbacks = new ConcurrentHashMap<Long, CompletionCallback<Object>>();
		currRequestID = new AtomicLong(0);
		channelHandler = new ChannelHandler(delegate, this);
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		// TODO: get method from endpoint and invoke instead of just doing connect
		if (method.getDeclaringClass() == ClientEndpoint.class) {
			connect(args[0], args[1], args[2]);
			return null;
		} else if (method.getDeclaringClass() == ServerEndpoint.class) {
			return null;
		} else {
			CompletionCallback<Object> cb;
			Object lastArg =  args[args.length - 1];
			
			if (lastArg instanceof CompletionCallback) {
				cb = (CompletionCallback<Object>)lastArg;
			} else {
				cb = null;
			}
			
			Request request = 
				new Request(
						currRequestID.incrementAndGet(),
						method.getName(),
						Arrays.copyOfRange(args, 0, args.length - 1),
						cb != null);
			if (cb != null) {
				callbacks.put(request.getId(), cb);
			}

			channel.write(request);

			if (method.getReturnType().isPrimitive()) {
				return ObjectUtils.createNullInstanceOf(method.getReturnType());
			}
			
			return null;
		}
	}
	
	private void connect(Object host, Object port, final Object cb) {
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
	
	private void listen(Object host, Object port) {
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
		bootstrap.bind(new InetSocketAddress((String)host, (Integer)port));
	}
	
	public void returnReceived(Return returnData) {
		long id = returnData.getId();
		CompletionCallback<Object> cb = callbacks.remove(id);
		cb.operationCompleted(returnData.decodeReturnVal());
	}
}