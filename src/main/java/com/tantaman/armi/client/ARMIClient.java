package com.tantaman.armi.client;

import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;

import com.tantaman.armi.ChannelHandler;

/**
 * This class is used to create the client side of an ARMI connection.
 * 
 * ARMI clients are constructed with a class that represents the interface of the server
 * on which methods should be invoked.  The returned ARMI Client is an {@link IClientEndpoint}.
 * 
 * Using the {@link IClientEndpoint} one can get a handle on the server methods to invoke.
 * @author tantaman
 *
 */
public class ARMIClient {	

	/**
	 * Creates an {@link IClientEndpoint} that provides a connection to a server with
	 * the provided interface.
	 * 
	 * E.g., if your server provides an implementation of the {@link java.util.List} interface
	 * you would call: <code>ARMIClient.create(List.class)</code> and then you would have
	 * access to the List methods of the server.
	 * 
	 * @param serverInterface The interface of the server to connect to
	 * @return {@link IClientEndpoint} capable of communicating with a server that implements serverInterface.
	 */
	public static <S> IClientEndpoint<S, Void> create(Class<S> serverInterface) {
		return create(serverInterface, null);
	}
	
	/**
	 * Creates an {@link IClientEndpoint} which can be called by the server using the methods
	 * provided in clientInterface.
	 * 
	 * Here is a contrived example:
	 * If your server implements {@link Callable} and your client implements {@link Runnable}
	 * then you would call this method like so:
	 * <code>
	 * clientEndpoint = ARMIClient.create(Callable.class, Runnable.class);
	 * </code>
	 * 
	 * Your client will then be able to invoke <code>clientEndpoint.getServerMethods().call()</code>
	 * to call the server.  In your server you will be able to invoke <code>server.getClientMethods.run()</code>
	 * to call the client.
	 * 
	 * @param serverInterface Interface of the server to connect to
	 * @param clientInterface Names of methods to be used by the server when calling the client
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <S, C> IClientEndpoint<S, C> create(Class<S> serverInterface, Class<C> clientInterface) {
		Object clientImpl = null;
		ClientRegistrationWrapper<C> clientRegistrations = new ClientRegistrationWrapper<C>();
		if (clientInterface != null) {
			clientImpl = Proxy.newProxyInstance(
					ARMIClient.class.getClassLoader(),
					new Class [] {clientInterface},
					clientRegistrations);
		}
		
		ChannelHandler channelHandler = new ChannelHandler(clientImpl);
		ClientEndpoint<S, C> endpoint = new ClientEndpoint<S, C>(channelHandler, clientRegistrations);
		ClientInvocationHandler clientInvokeHandler =  new ClientInvocationHandler(endpoint);
		channelHandler.setReturnHandler(clientInvokeHandler);
		
		S remoteServer = (S)Proxy.newProxyInstance(ARMIClient.class.getClassLoader(),
				new Class [] {serverInterface},
				clientInvokeHandler);
		
		endpoint.setServerMethods(remoteServer);
		
		return endpoint;
	}
}
