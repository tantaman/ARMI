package com.tantaman.armi.client;

import java.lang.reflect.Proxy;

import com.tantaman.armi.ARMInvocationHandler;

public class ARMIClient {	
	// TODO: allow client interface to be passed here for solid type checking?
	@SuppressWarnings("unchecked")
	public static <T> T create(Class<T> serverInterface, Class<T> clientInterface) {
		Object clientDelegate = null;
		if (clientInterface != null) {
			clientDelegate = Proxy.newProxyInstance(
					Thread.currentThread().getContextClassLoader(),
					new Class [] {clientInterface},
					new ClientRegistrationWrapper());
		}
		return (T)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
				new Class [] {serverInterface, ClientEndpoint.class},
				new ARMInvocationHandler(clientDelegate));
	}
}
