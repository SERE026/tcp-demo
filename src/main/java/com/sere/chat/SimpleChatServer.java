package com.sere.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class SimpleChatServer {
	private int port ;
	
	public SimpleChatServer(int port){
		this.port = port;
	}
	
	public void run(){
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		
		ServerBootstrap server = new ServerBootstrap();
		server.group(bossGroup, workGroup)
		.channel(NioServerSocketChannel.class)
		.childHandler(new SimpleChatServerInitializer())
		.option(ChannelOption.SO_BACKLOG, 128)
		.childOption(ChannelOption.SO_KEEPALIVE, true);
		
		
		System.out.println("服务器启动了.......");
		
		try {
			ChannelFuture cf = server.bind(port).sync();
			cf.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
		
	}
	
	public static void main(String[] args) {
		SimpleChatServer s = new SimpleChatServer(18092);
		s.run();
	}
}
