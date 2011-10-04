package com.tantaman.armi.examples.chat;

import com.tantaman.armi.CompletionCallback;

public interface IChatServer {
	void newMessage(ChatMessage message, CompletionCallback<String> cb);
}
