package com.tantaman.armi.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.tantaman.armi.CompletionCallback;
import com.tantaman.armi.Request;
import com.tantaman.armi.Return;
import com.tantaman.commons.lang.ObjectUtils;

public class ServerChannelHandler extends SimpleChannelUpstreamHandler {
	private final Object delegate;
	
	// TODO: do an initial handshake where we get the interface being
	// used by the client and set up method ids?
	public ServerChannelHandler(Object delegate) {
		this.delegate = delegate;
	}
	
	// TODO: handle 'registerClient' message.
	@Override
	public void messageReceived(final ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
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
}
