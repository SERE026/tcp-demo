package com.sere.chat1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class WebsocketChatServer {
	private int port;

	public WebsocketChatServer(int port) {
		super();
		this.port = port;
	}
	
	public void run(){
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		
		try{
		
			ServerBootstrap server = new ServerBootstrap();
			server.group(bossGroup, workGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new WebsocketChatServerInitializer())
			.option(ChannelOption.SO_BACKLOG, 128)
			.childOption(ChannelOption.SO_KEEPALIVE, true);
			
			System.out.println("服务器启动成功......");
			
			//绑定端口
			ChannelFuture future = server.bind(port).sync();
			future.channel().closeFuture().sync();
		}catch (Exception e) {
			// TODO: handle exception
		}finally{
			workGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) {
		WebsocketChatServer server = new WebsocketChatServer(18093);
		server.run();
	}
}
