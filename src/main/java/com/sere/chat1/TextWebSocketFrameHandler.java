package com.sere.chat1;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

	private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
		// TODO Auto-generated method stub
		Channel incoming = ctx.channel();
		for(Channel channel : channels){
			if(channel != incoming){
				channel.writeAndFlush(new TextWebSocketFrame("["+incoming.remoteAddress()+"]: "+msg.text()));
			}else{
				channel.writeAndFlush(new TextWebSocketFrame("[you]: "+msg.text()));
			}
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		Channel incoming = ctx.channel();
		System.out.println("Cilent: ["+incoming.remoteAddress()+"] 在线");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		Channel incoming = ctx.channel();
		System.out.println("Cilent: ["+incoming.remoteAddress()+"] 掉线");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		Channel incoming = ctx.channel();
		ctx.writeAndFlush(new TextWebSocketFrame("[SERVER]-"+incoming.remoteAddress()+" 加入\n"));
		channels.add(incoming);
		System.out.println("Cilent: ["+incoming.remoteAddress()+"] 加入");
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		Channel incoming = ctx.channel();
		ctx.writeAndFlush(new TextWebSocketFrame("[SERVER]-"+incoming.remoteAddress()+" 离开\n"));
		System.out.println("Cilent: ["+incoming.remoteAddress()+"] 离开");
	}


}
