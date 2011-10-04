package com.tantaman.armi.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class ClientRegistrationWrapper<T> implements InvocationHandler {
	private final Set<T> registrations;
	
	public ClientRegistrationWrapper() {
		registrations = new CopyOnWriteArraySet<T>();
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		// 1. look up the method
		// 2. invoke the method on each registration
		return null;
	}

}
