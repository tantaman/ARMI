package com.tantaman.armi.examples;

import com.tantaman.armi.ARMIServer;

public class ServerApp {
	public static void main(String[] args) {
		ARMIServer server = ARMIServer.create(new Server());
		
		server.listen("localhost", 2435);
	}
}
