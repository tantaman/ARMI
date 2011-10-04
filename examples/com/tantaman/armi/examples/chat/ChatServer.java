package com.tantaman.armi.examples.chat;

import com.tantaman.armi.CompletionCallback;
import com.tantaman.armi.server.ARMIServer;
import com.tantaman.armi.server.IServerEndpoint;

public class ChatServer implements IChatServer {
	private final IChatClient remoteClients;
	public static void main(String[] args) {
		String host;
		int port;
		if (args.length == 0) {
			host = "localhost";
			port = 2345;
		} else {
			host = args[0];
			port = Integer.parseInt(args[1]);
		}
		
		new ChatServer(host, port);
	}
	
	public ChatServer(String host, int port) {
		remoteClients = ARMIServer.create(this, IChatClient.class);
		((IServerEndpoint)remoteClients).ARMIlisten(host, port);
	}

	@Override
	public void newMessage(ChatMessage message, CompletionCallback<String> cb) {
		// broadcast out to all connected clients.
		System.out.println("SERVER GOT MESSAGE " + message);
		remoteClients.newMessage(message);
		cb.operationCompleted("CALLBACK");
	}
}
