package com.tantaman.armi;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.tantaman.commons.lang.ObjectUtils;

public class ChannelHandler extends SimpleChannelUpstreamHandler {
	// Delegate is 1 object on the server side
	// or a wrapper around several client registrations on the client side.
	/**
	 * Handles method calls from the remote side.
	 */
	private final Object delegate;
	/**
	 * Handles method returns from the remote side
	 * Shouldn't we make a callback or return handler class for that?  InvocationHandler
	 * doing it seems odd.
	 */
	private final ARMInvocationHandler invocationHandler;
	
	// TODO: do an initial handshake where we get the interface being
	// used by the client and set up method ids?
	public ChannelHandler(Object delegate, ARMInvocationHandler invocationHandler) {
		this.delegate = delegate;
		this.invocationHandler = invocationHandler;
	}
	
	@Override
	public void messageReceived(final ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		if (e.getMessage() instanceof Request) {
			handleMethodCall(ctx, e);
		} else {
			handleMethodReturn(ctx, e);
		}
	}
	
	private void handleMethodCall(final ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		final Request req = (Request)e.getMessage();
		 
		Class<?> [] types;
		Object [] args;
		if (req.expectsResponse()) {
			types = ObjectUtils.getTypes(req.getArgs(), CompletionCallback.class);
			args = new Object[req.getArgs().length + 1];
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
		} else {
			types = ObjectUtils.getTypes(req.getArgs());
			args = req.getArgs();
		}
		
		delegate.getClass()
		.getMethod(req.getMethod(), types)
		.invoke(delegate, args);
	}
	
	private void handleMethodReturn(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		invocationHandler.returnReceived((Return)e.getMessage());
	}
}
