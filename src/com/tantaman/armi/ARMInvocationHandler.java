package com.tantaman.armi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.channel.Channel;

import com.tantaman.commons.lang.ObjectUtils;

// TODO: add ability to register a client
// so it can be called from the server
public abstract class ARMInvocationHandler implements InvocationHandler {
	private final Map<Long, CompletionCallback<Object>> callbacks;
	protected final AtomicLong currRequestID;

	public ARMInvocationHandler() {
		callbacks = new ConcurrentHashMap<Long, CompletionCallback<Object>>();
		currRequestID = new AtomicLong(0);
	}

	protected abstract Channel getChannel();

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
	throws Throwable {
		CompletionCallback<Object> cb;
		Object lastArg =  args[args.length - 1];
		Request request;

		if (lastArg instanceof CompletionCallback) {
			cb = (CompletionCallback<Object>)lastArg;
			request = 
				new Request(
						currRequestID.incrementAndGet(),
						method.getName(),
						Arrays.copyOfRange(args, 0, args.length - 1),
						true);
		} else {
			cb = null;
			request = 
				new Request(
						currRequestID.incrementAndGet(),
						method.getName(),
						args,
						false);
		}
		
		if (cb != null) {
			callbacks.put(request.getId(), cb);
		}

		getChannel().write(request);

		return ObjectUtils.createNullInstanceOf(method.getReturnType());
	}

	public void returnReceived(Return returnData) {
		long id = returnData.getId();
		CompletionCallback<Object> cb = callbacks.remove(id);
		cb.operationCompleted(returnData.decodeReturnVal());
	}
}