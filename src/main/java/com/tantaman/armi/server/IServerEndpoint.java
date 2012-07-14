package com.tantaman.armi.server;

/**
 * Represents the server side of an ARMI connection.
 * 
 * {@link IServerEndpoint} provides protocol level methods to control
 * an ARMI server.  IServerEndpoint also provides a mean to
 * call registered clients.
 * 
 * @author tantaman
 *
 * @param <T>
 */
public interface IServerEndpoint<T> {
	/**
	 * Start listening for connections on the provided host (localhost) and port
	 * @param host
	 * @param port
	 */
	public void listen(String host, int port);
	/**
	 * Stop the server.
	 */
	public void shutdown();
	/**
	 * Get the methods by which to make calls on clients that are registered with
	 * the server.
	 * @return
	 */
	public T getClientMethods();
}
