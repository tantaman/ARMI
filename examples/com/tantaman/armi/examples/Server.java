package com.tantaman.armi.examples;

import com.tantaman.armi.CompletionCallback;

public class Server implements ServerInterface {
		@Override
		public long getTime(String locality, CompletionCallback<Long> cb) {
			System.out.println("Locality received: " + locality);
			return 11;
		}
		
		@Override
		public String getNameOf(String relation, CompletionCallback<String> cb) {
			System.out.println("Relation received: " + relation);
			String retVal = "Marie Lou";
			cb.operationCompleted(retVal);
			return retVal;
		}
}
