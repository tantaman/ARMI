package com.tantaman.armi;

import org.jboss.netty.channel.ChannelFuture;

public interface Endpoint {
	public ChannelFuture connect(String host, int port);
}
