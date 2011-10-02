package com.tantaman.armi.examples;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import com.tantaman.armi.ARMIClient;
import com.tantaman.armi.Endpoint;

public class ClientApp {
	public static void main(String[] args) {
		final ServerInterface remote = ARMIClient.create(ServerInterface.class);
		
		ChannelFuture f = ((Endpoint)remote).connect("localhost", 2435);
		
		f.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				remote.getNameOf("sister", null);
				remote.getTime("NY", null);
			}
		});
	}
}
