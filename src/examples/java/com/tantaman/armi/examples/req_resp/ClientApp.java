package com.tantaman.armi.examples.req_resp;

import java.util.concurrent.atomic.AtomicLong;

import com.tantaman.armi.CompletionCallback;
import com.tantaman.armi.client.ARMIClient;
import com.tantaman.armi.client.IClientEndpoint;

public class ClientApp {
	private static final AtomicLong numReturns = new AtomicLong(0);
	private static final int NUM_CALLS = 20000;
	private static int iters;
	private static final int NUM_ITERS = 100;
	
	public static void main(String[] args) {
		final IClientEndpoint<ServerInterface, Void> remote = ARMIClient.create(ServerInterface.class, null);

		remote.connect("localhost", 2435, new CompletionCallback<Void>() {
			@Override
			public void operationCompleted(Void retVal) {
				start(remote.getServerMethods());
			}
		});
	}

	private static void start(final ServerInterface remote) {
		final long startTime = System.currentTimeMillis();
		for (int i = 0; i < NUM_CALLS; ++i) {
			remote.getTime("NY", new CompletionCallback<String>() {
				@Override
				public void operationCompleted(String retVal) {
					long returns = numReturns.incrementAndGet();
					if (returns % NUM_CALLS == 0) {
						long endTime = System.currentTimeMillis();
						System.out.println("DONE " + NUM_CALLS + " CALLS IN " + (endTime - startTime) + " ms");
						if (iters < NUM_ITERS) {
							++iters;
							start(remote);
						}
					}
				}
			});
		}
	}
}
