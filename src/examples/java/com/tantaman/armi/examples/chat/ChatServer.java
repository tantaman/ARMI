package com.tantaman.armi.examples.chat;

import com.tantaman.armi.server.ARMIServer;
import com.tantaman.armi.server.IServerEndpoint;

public class ChatServer implements IChatServer {
	// TODO: return a server endpoint from ARMI.create and have a "getClientMethods" call on that
	private final IServerEndpoint<IChatClient> server;
	public ChatServer(String host, int port) {
		server = ARMIServer.create(this, IChatClient.class);
		server.listen(host, port);
	}

	@Override
	public void messageReceived(ChatMessage msg) {
		server.getClientMethods().messageReceived(msg);
	}
}
