package com.tantaman.armi.client;

import com.tantaman.armi.CompletionCallback;

public interface ClientEndpoint {
	public void ARMIconnect(String host, int port, CompletionCallback<Void> cb);
	public boolean ARMIshutdown();
	public void ARMIregisterClient(Object client);
}
