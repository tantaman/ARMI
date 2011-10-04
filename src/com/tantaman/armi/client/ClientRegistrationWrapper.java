package com.tantaman.armi.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.tantaman.commons.lang.ObjectUtils;

public class ClientRegistrationWrapper<T> implements InvocationHandler {
	private final Set<T> registrations;
	
	public ClientRegistrationWrapper() {
		registrations = new CopyOnWriteArraySet<T>();
	}
	
	public void add(T client) {
		registrations.add(client);
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		for (T client : registrations) {
			method.invoke(client, args);
		}
		
		// TODO: return values from client to server?
		return ObjectUtils.createNullInstanceOf(method.getReturnType());
	}
}
