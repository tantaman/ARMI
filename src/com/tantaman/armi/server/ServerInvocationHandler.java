package com.tantaman.armi.server;

import java.lang.reflect.Method;

import org.jboss.netty.channel.Channel;

import com.tantaman.armi.ARMInvocationHandler;

public class ServerInvocationHandler extends ARMInvocationHandler {
	private final ServerEndpoint serverEndpoint;
	
	public ServerInvocationHandler(ServerEndpoint serverEndpoint) {
		super();
		this.serverEndpoint = serverEndpoint; 
	}

	@Override
	protected Channel getChannel() {
		return serverEndpoint.getChannel();
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if (method.getDeclaringClass() == IServerEndpoint.class) {
			serverEndpoint.ARMIlisten((String)args[0], (Integer)args[1]);
			return null;
		} else {
			return super.invoke(proxy, method, args);
		}
	}
}
