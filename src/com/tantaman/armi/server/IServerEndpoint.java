package com.tantaman.armi.server;

public interface IServerEndpoint<T> {
	public void listen(String host, int port);
	public void shutdown();
	public T getClientMethods();
}
