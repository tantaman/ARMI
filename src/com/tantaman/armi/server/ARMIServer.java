package com.tantaman.armi.server;

import java.lang.reflect.Proxy;

import com.tantaman.armi.ChannelHandler;


public class ARMIServer {
	@SuppressWarnings("unchecked")
	public static <C> C create(Object serverImpl, Class<C> clientInterface) {
		Object remoteClient = null;
		ChannelHandler channelHandler = new ChannelHandler(serverImpl);
		ServerEndpoint serverEndpoint = new ServerEndpoint(channelHandler);
		ServerInvocationHandler invokeHandler = new ServerInvocationHandler(serverEndpoint);
		channelHandler.setReturnHandler(invokeHandler);
		
		remoteClient = Proxy.newProxyInstance(
				Thread.currentThread().getContextClassLoader(),
				new Class [] {clientInterface, IServerEndpoint.class},
				invokeHandler);

		return (C)remoteClient;
	}
	
	public static IServerEndpoint create(Object serverImpl) {
		return new ServerEndpoint(new ChannelHandler(serverImpl));
	}
}
