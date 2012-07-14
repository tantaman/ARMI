package com.tantaman.armi.client;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.jboss.netty.channel.Channel;

import com.tantaman.armi.ARMInvocationHandler;

/**
 * ARMI generates proxies to handle invocations of server methods from the client
 * and client methods from the server.  This is the invocation handler for
 * the {@link Proxy}.
 * 
 * @author tantaman
 *
 */
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
