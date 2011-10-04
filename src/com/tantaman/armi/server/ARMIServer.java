package com.tantaman.armi.server;

import java.lang.reflect.Proxy;


public class ARMIServer {
	@SuppressWarnings("unchecked")
	public static <C> IServerEndpoint<C> create(Object serverImpl, Class<C> clientInterface) {
		Object remoteClient = null;
		ServerChannelHandler channelHandler = new ServerChannelHandler(serverImpl);
		ServerEndpoint<C> serverEndpoint = new ServerEndpoint<C>(channelHandler);
		ServerInvocationHandler invokeHandler = new ServerInvocationHandler(serverEndpoint);
		channelHandler.setReturnHandler(invokeHandler);
		
		remoteClient = Proxy.newProxyInstance(
				ARMIServer.class.getClassLoader(),
				new Class [] {clientInterface},
				invokeHandler);
		
		serverEndpoint.setClientMethods((C)remoteClient);

		return serverEndpoint;
	}
	
	public static IServerEndpoint<?> create(Object serverImpl) {
		return new ServerEndpoint<Void>(new ServerChannelHandler(serverImpl));
	}
}
