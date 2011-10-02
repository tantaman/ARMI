package com.tantaman.armi;

public interface CompletionCallback<T> {
	public void operationCompleted(T retVal);
}
