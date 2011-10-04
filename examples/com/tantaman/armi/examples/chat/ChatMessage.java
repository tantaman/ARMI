package com.tantaman.armi.examples.chat;

import java.io.Serializable;

public class ChatMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String username;
	private final String text;
	
	public ChatMessage(String username, String text) {
		this.username = username;
		this.text = text;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getText() {
		return text;
	}
	
	@Override
	public String toString() {
		return username + ": " + text;
	}
}
