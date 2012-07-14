package com.tantaman.armi.examples.chat;

import java.util.Scanner;

import com.tantaman.armi.CompletionCallback;
import com.tantaman.armi.client.ARMIClient;
import com.tantaman.armi.client.IClientEndpoint;

public class ChatClient implements IChatClient {
	private final IClientEndpoint<IChatServer, IChatClient> remoteServer;
	public ChatClient(String host, int port) {
		remoteServer = ARMIClient.create(IChatServer.class, IChatClient.class);

		remoteServer.connect(host, port, new CompletionCallback<Void>() {
			@Override
			public void operationCompleted(Void retVal) {
				start();
			}
		});
	}
	
	private void start() {
		new Thread() {
			public void run() {
				remoteServer.registerClient(ChatClient.this);
				Scanner input = new Scanner(System.in);
				
				System.out.print("Username? ");
				String username = input.nextLine();
				
				do {
					System.out.print("> ");
					String text = input.nextLine();
					remoteServer.getServerMethods().messageReceived(new ChatMessage(username, text));
				} while (true);
			}
		}.start();
	}
	
	@Override
	public void messageReceived(ChatMessage message) {
		System.out.println(message);
	}
}
