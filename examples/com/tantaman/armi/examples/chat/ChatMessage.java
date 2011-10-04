package com.tantaman.armi.examples.chat;

import java.io.Serializable;

public class ChatMessage implements Serializable {
	private final String username;
	private final String text;
	
	public ChatMessage(String username, String text) {
		this.username = username;
		this.text = text;
	}

	public String getText() {
		return text;
	}
	
	public String getUsername() {
		return username;
	}
	
	@Override
	public String toString() {
		return username + ": " + text + "\n";
	}
}
