package com.tantaman.armi.client;

import com.tantaman.armi.CompletionCallback;

public interface IClientEndpoint<S, C> {
	public void ARMIconnect(String host, int port, CompletionCallback<Void> cb);
	public boolean ARMIshutdown();
	public void ARMIregisterClient(C client);
	public S getServerMethods();
}
