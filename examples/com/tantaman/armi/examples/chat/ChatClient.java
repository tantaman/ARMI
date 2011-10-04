package com.tantaman.armi.examples.chat;

import java.util.Scanner;

import com.tantaman.armi.CompletionCallback;
import com.tantaman.armi.client.ARMIClient;
import com.tantaman.armi.client.IClientEndpoint;

public class ChatClient implements IChatClient {
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
		
		
		new ChatClient(host, port);
	}
	
	private final IChatServer remoteServer;
	private String username;
	
	public ChatClient(String host, int port) {
		remoteServer = ARMIClient.create(IChatServer.class, IChatClient.class);
		((IClientEndpoint<IChatClient>)remoteServer).ARMIconnect(host, port, new CompletionCallback<Void>() {
			@Override
			public void operationCompleted(Void retVal) {
				startMainLoop();
			}
		});
	}
	
	private void startMainLoop() {
		((IClientEndpoint<IChatClient>)remoteServer).ARMIregisterClient(this);
		Scanner input = new Scanner(System.in);
		
		System.out.println("Your username? ");
		username = input.nextLine();
		
		while (input.hasNext()) {
			String text = input.nextLine();
			ChatMessage message = new ChatMessage(username, text);
			remoteServer.newMessage(message, new CompletionCallback<String>() {
				@Override
				public void operationCompleted(String retVal) {
					System.out.println(retVal);
				}
			});
		}
	}
	
	public void newMessage(ChatMessage message) {
		// filter out messages from ourselves (should handle this at the server, but this is a simple demo)
//		if (!message.getUsername().equals(username))
			System.out.println(message);
	}
}
