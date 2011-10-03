package com.tantaman.armi.server;

public interface ServerEndpoint {
	public void ARMIlisten(String host, int port);
	public boolean ARMIshutdown();
}
