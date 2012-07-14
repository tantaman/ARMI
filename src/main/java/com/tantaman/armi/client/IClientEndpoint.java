package com.tantaman.armi.client;

import com.tantaman.armi.CompletionCallback;

/**
 * Provides an interface for the client to invoke protocol
 * level methods on the server as well as obtain access
 * to the methods specified in the server interface.
 * 
 * @author tantaman
 *
 * @param <S>
 * @param <C>
 */
public interface IClientEndpoint<S, C> {
	/**
	 * Connect to the server at the specified host and port.
	 * When the connection is established cb will be invoked.
	 * @param host Host of the server
	 * @param port Port of the server
	 * @param cb Callback to invoke when the connection is established
	 */
	public void connect(String host, int port, CompletionCallback<Void> cb);
	
	/**
	 * Ends the connection.
	 * @return
	 */
	public boolean shutdown();
	
	/**
	 * Register a client so it can be called by the server.
	 * @param client
	 */
	public void registerClient(C client);
	
	/**
	 * 
	 * @return Implementation of the serverInterface provided to {@link ARMIClient}.create that can be used
	 * to invoke methods on the server. 
	 */
	public S getServerMethods();
}
