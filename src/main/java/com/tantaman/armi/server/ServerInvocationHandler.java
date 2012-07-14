package com.tantaman.armi.server;

import java.lang.reflect.Method;

import org.jboss.netty.channel.Channel;

import com.tantaman.armi.ARMInvocationHandler;
import com.tantaman.armi.Request;
import com.tantaman.commons.lang.ObjectUtils;

public class ServerInvocationHandler extends ARMInvocationHandler {
	private final ServerEndpoint<?> serverEndpoint;
	
	public ServerInvocationHandler(ServerEndpoint<?> serverEndpoint) {
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
			serverEndpoint.listen((String)args[0], (Integer)args[1]);
			return null;
		} else {
			Request request;
			request = 
				new Request(
						currRequestID.incrementAndGet(),
						method.getName(),
						args,
						false);
			
			for (Channel channel : serverEndpoint.getChannels()) {
				channel.write(request);
			}
			
			return ObjectUtils.createNullInstanceOf(method.getReturnType());
		}
	}
}
