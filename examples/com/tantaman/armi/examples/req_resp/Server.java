package com.tantaman.armi.examples.req_resp;

import com.tantaman.armi.CompletionCallback;

public class Server implements ServerInterface {
		@Override
		public void getTime(String locality, CompletionCallback<Long> cb) {
			cb.operationCompleted(11L);
		}
		
		@Override
		public void getNameOf(String relation, CompletionCallback<String> cb) {
			String retVal = "Marie Lou";
			cb.operationCompleted(retVal);
		}
}
