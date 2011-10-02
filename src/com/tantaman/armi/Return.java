package com.tantaman.armi;

import java.io.Serializable;

// TODO: template?
public class Return implements Serializable {
	private final long id;
//	private final byte [] payload;
	private final Object returnVal;
	
	public Return(long id, Object returnVal) {
		this.id = id;
//		this.payload = encodeReturnVal(returnVal);
		this.returnVal = returnVal;
	}
	
	public long getId() {
		return id;
	}
	
	public Object getReturnVal() {
		return returnVal;
	}
	
	public Object decodeReturnVal() {
		return null;
	}
	
	private byte [] encodeReturnVal(Object val) {
		return null;
	}
}
