package com.tantaman.armi.server;

import java.lang.reflect.Proxy;


/**
 * Creates the server side of an ARMI connection.
 * @author tantaman
 *
 */
public class ARMIServer {
	
	/**
	 * Creates an {@link IServerEndpoint} backed by the provided server implementation and able
	 * to call the methods specified by clientInterface on the client.
	 * @param serverImpl POJO server implementation
	 * @param clientInterface Client methods that the server should have access to
	 * @return {@link IServerEndpoint} used to start the server and access clients.
	 */
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
	
	/**
	 * Creates an {@link IServerEndpoint} backed by the provided server implementation.
	 * The server implementation is just a POJO whose methods will be called
	 * when a method invocation is made by a client.
	 * @param serverImpl
	 * @return {@link IServerEndpoint} used to start the server
	 */
	public static IServerEndpoint<?> create(Object serverImpl) {
		return new ServerEndpoint<Void>(new ServerChannelHandler(serverImpl));
	}
}
