package com.tantaman.armi.examples.req_resp;

import com.tantaman.armi.server.ARMIServer;
import com.tantaman.armi.server.IServerEndpoint;

public class ServerApp {
	public static void main(String[] args) {
		IServerEndpoint<?> server = ARMIServer.create(new Server());
		server.listen("localhost", 2435);
	}
}
