package com.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousServerSocketChannel;



/**
 * 基于NIO2 读写模式的服务端
 * http://codepub.cn/2016/02/26/Asynchronous-non-blocking-message-communication-framework-based-on-Java-NIO2/
 * @author sere
 *
 */
public class ServerOnReaderAndWriter {
	
	static final int PORT = 7777;
	static final String IP = "localhost";
	
	public static void main(String[] args) {
		
		try(AsynchronousServerSocketChannel serverSockChannel = AsynchronousServerSocketChannel.open()){
			//服务端的通道支持两种选项SO_RCVBUF和SOREUSEADDR，一般无需显示设置，使用默认即可
			//在面向流的通道中，此选项表示在前一个连接处于TIME_WAIT状态时，下一个通道是否可以重用的通道地址
			serverSockChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			//设置通道接收的字节大小
			serverSockChannel.setOption(StandardSocketOptions.SO_RCVBUF, 8*1024);
			
			serverSockChannel.bind(new InetSocketAddress(IP, PORT));
			
			System.out.println("Waiting for connections...");
			serverSockChannel.accept(serverSockChannel, new Acceptor());
			
			System.in.read();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
