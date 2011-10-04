package com.tantaman.armi.server;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;

import com.tantaman.armi.ChannelHandler;

public class ServerChannelHandler extends ChannelHandler {
	// TODO: can I rely on channel instances being the same?
	// or should I map on channel id?
	private final Set<Channel> channels;
	
	public ServerChannelHandler(Object delegate) {
		super(delegate);
		channels = new CopyOnWriteArraySet<Channel>();
	}
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		channels.add(ctx.getChannel());
	}
	
	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		channels.remove(ctx.getChannel());
	}
	
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		channels.remove(ctx.getChannel());
	}
	
	public Set<Channel> getChannels() {
		return channels;
	}
}
