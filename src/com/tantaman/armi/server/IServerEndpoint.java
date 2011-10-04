package com.tantaman.armi.server;

public interface IServerEndpoint {
	public void ARMIlisten(String host, int port);
	public void ARMIshutdown();
}
