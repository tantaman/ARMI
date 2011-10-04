package com.tantaman.armi.client;

import java.lang.reflect.Proxy;

import com.tantaman.armi.ChannelHandler;

public class ARMIClient {	
	// TODO: allow client interface to be passed here for solid type checking?
	@SuppressWarnings("unchecked")
	public static <S, C> S create(Class<S> serverInterface, Class<C> clientInterface) {
		Object clientImpl = null;
		ClientRegistrationWrapper<C> clientRegistrations = new ClientRegistrationWrapper<C>();
		if (clientInterface != null) {
			clientImpl = Proxy.newProxyInstance(
					Thread.currentThread().getContextClassLoader(),
					new Class [] {clientInterface},
					clientRegistrations);
		}
		
		ChannelHandler channelHandler = new ChannelHandler(clientImpl);
		ClientEndpoint endpoint = new ClientEndpoint(channelHandler, clientRegistrations);
		ClientInvocationHandler clientInvokeHandler =  new ClientInvocationHandler(endpoint);
		channelHandler.setReturnHandler(clientInvokeHandler);
		
		S remoteServer = (S)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
				new Class [] {serverInterface, IClientEndpoint.class},
				clientInvokeHandler);
		
		return remoteServer;
	}
}
