package com.tantaman.armi.client;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.tantaman.armi.ARMInvocationHandler;
import com.tantaman.armi.Return;

public class ClientChannelHandler extends SimpleChannelUpstreamHandler {
	private final ARMInvocationHandler invocationHandler;
	
	public ClientChannelHandler(ARMInvocationHandler invocationHandler) {
		this.invocationHandler = invocationHandler;
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		invocationHandler.returnReceived((Return)e.getMessage());
	}
}
