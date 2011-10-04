package com.tantaman.armi.client;

import com.tantaman.armi.CompletionCallback;

public interface IClientEndpoint<S, C> {
	public void connect(String host, int port, CompletionCallback<Void> cb);
	public boolean shutdown();
	public void registerClient(C client);
	public S getServerMethods();
}
