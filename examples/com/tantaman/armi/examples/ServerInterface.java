package com.tantaman.armi.examples;

import com.tantaman.armi.CompletionCallback;


public interface ServerInterface {
	void getNameOf(String relation, CompletionCallback<String> cb);
	void getTime(String locality, CompletionCallback<Long> cb);
}
