package com.tantaman.armi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;


public class ARMIClient {
	// TODO: this should implement a common base
	// so we can allow bi-di communication
	private static class Handler extends SimpleChannelUpstreamHandler implements InvocationHandler {
		private final Map<Long, CompletionCallback<Object>> callbacks;
		private final AtomicLong currRequestID;
		private volatile Channel channel;
		
		public Handler() {
			callbacks = new ConcurrentHashMap<Long, CompletionCallback<Object>>();
			currRequestID = new AtomicLong(0);
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			if (method.getDeclaringClass() == Endpoint.class) {
				return connect(args[0], args[1]);
			} else {
				CompletionCallback<Object> cb = (CompletionCallback<Object>)args[args.length - 1];
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

				// TODO: be smarter about creating the return.  This gives us a boxing
				// error from int to long...
				if (method.getReturnType().isPrimitive()) {
					return method.getReturnType().newInstance();
				}
				
				return null;
			}
		}
		
		private Object connect(Object host, Object port) {
			String sHost = (String)host;
			int iPort = (Integer)port;

			// Configure the client.
			ClientBootstrap bootstrap = new ClientBootstrap(
					new NioClientSocketChannelFactory(
							Executors.newCachedThreadPool(),
							Executors.newCachedThreadPool()));

			// Set up the pipeline factory.
			bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
				public ChannelPipeline getPipeline() throws Exception {
					return Channels.pipeline(
							new ObjectEncoder(),
							new ObjectDecoder(),
							Handler.this);
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
			
			return f;
		}
		
		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
				throws Exception {
			returnReceived((Return)e.getMessage());
		}
		
		private void returnReceived(Return returnData) {
			System.out.println("GOT A RETURN DAWG!");
			long id = returnData.getId();
			CompletionCallback<Object> cb = callbacks.remove(id);
			cb.operationCompleted(returnData.decodeReturnVal());
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T create(Class<T> serverInterface) {
		return (T)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
				new Class [] {serverInterface, Endpoint.class},
				new Handler());
	}
}
