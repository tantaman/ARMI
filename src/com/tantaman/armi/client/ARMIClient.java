package com.tantaman.armi.client;

import java.lang.reflect.Proxy;

import com.tantaman.armi.ChannelHandler;

public class ARMIClient {	

	public static <S> IClientEndpoint<S, Void> create(Class<S> serverInterface) {
		return create(serverInterface, null);
	}
	
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
