package com.sere.chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class SimpleChatClient {
	private String host;
	private int port ;
	
	public SimpleChatClient(String host,int port){
		this.host = host;
		this.port = port;
	}
	
	public void run(){
		EventLoopGroup group = new NioEventLoopGroup();
		
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(group)
		.channel(NioSocketChannel.class)
		.handler(new SimpleChatClientInitializer());
		
		Channel channel;
		try {
			channel = bootstrap.connect(host,port).sync().channel();
		
		
			BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
			while(true){
				channel.writeAndFlush(buf.readLine()+"\r\n");
			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			group.shutdownGracefully();
		}
		
	}
	
	public static void main(String[] args) {
		SimpleChatClient s = new SimpleChatClient("localhost",18092);
		s.run();
	}
}
