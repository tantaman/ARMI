package com.tantaman.armi.examples.req_resp;

import com.tantaman.armi.CompletionCallback;

public class Server implements ServerInterface {
		@Override
		public void getTime(String locality, CompletionCallback<String> cb) {
			cb.operationCompleted("COMPL");
		}
		
		@Override
		public void getNameOf(String relation, CompletionCallback<String> cb) {
			String retVal = "Marie Lou";
			cb.operationCompleted(retVal);
		}
}
