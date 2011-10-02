package com.tantaman.armi;

import java.io.Serializable;

// TODO: template?
public class Request implements Serializable {
	private final long id;
	private final String methodName;
	private final boolean expectsResponse;
	private final Object [] args;
//	private final byte [] payload;
	
	public Request(long id, String method, Object [] args, boolean expectsResponse) {
		this.id = id;
		this.methodName = method;
		this.args = args;
		this.expectsResponse = expectsResponse;
	}
	
	public long getId() {
		return id;
	}
	
	public String getMethod() {
		return methodName;
	}
	
	public boolean expectsResponse() {
		return expectsResponse;
	}
	
	public Object [] getArgs() {
		return args;
	}
	
//	public Object [] decodeArgs() {
//		return null;
//	}
//	
//	private byte [] encodeArgs(Object [] args) {
//		return null;
//	}
}
