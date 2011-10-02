package com.tantaman.armi.examples;

import com.tantaman.armi.CompletionCallback;


public interface ServerInterface {
	String getNameOf(String relation, CompletionCallback<String> cb);
	long getTime(String locality, CompletionCallback<Long> cb);
}
