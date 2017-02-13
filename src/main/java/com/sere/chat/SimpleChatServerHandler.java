package com.sere.chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.channel.ChannelHandler.Sharable;;

@Sharable
public class SimpleChatServerHandler extends SimpleChannelInboundHandler<String> {
	
	public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		//super.handlerAdded(ctx);
		Channel incoming = ctx.channel();
		channels.writeAndFlush("[SERVER]"+incoming.remoteAddress() + "加入\n");
		channels.add(ctx.channel());
		
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		//super.handlerRemoved(ctx);
		Channel incoming = ctx.channel();
		channels.writeAndFlush("[SERVER]"+incoming.remoteAddress() + "离开\n");
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		//super.channelActive(ctx);
		Channel incoming = ctx.channel();
		System.out.println("SimpleChatClient:"+incoming.remoteAddress() + "在线");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		//super.channelInactive(ctx);
		Channel incoming = ctx.channel();
		System.out.println("SimpleChatClient:"+incoming.remoteAddress() + "掉线");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		//super.exceptionCaught(ctx, cause);
		cause.printStackTrace();
		ctx.close();
	}


	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		// TODO Auto-generated method stub
		Channel incoming = ctx.channel();
		for(Channel channel : channels){
			if(channel == incoming){
				incoming.writeAndFlush("[you]" + msg + "\n");
			}else{
				channel.writeAndFlush("["+incoming.remoteAddress()+"]" + msg + "\n");
			}
		}
	}

}
