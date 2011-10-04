package com.tantaman.armi.client;

import java.lang.reflect.Method;

import org.jboss.netty.channel.Channel;

import com.tantaman.armi.ARMInvocationHandler;
import com.tantaman.armi.CompletionCallback;

public class ClientInvocationHandler extends ARMInvocationHandler {
	private final ClientEndpoint clientEndpoint;
	public ClientInvocationHandler(ClientEndpoint clientEndpoint) {
		super();
		this.clientEndpoint = clientEndpoint;
	}

	@Override
	protected Channel getChannel() {
		return clientEndpoint.getChannel();
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
	throws Throwable {
		if (method.getDeclaringClass() == IClientEndpoint.class) {
			return method.invoke(clientEndpoint, args);
		} else {
			return super.invoke(proxy, method, args);
		}
	}
}
