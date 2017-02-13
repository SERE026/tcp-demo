package com.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * 基于NIO2 读写模式的服务端
 * @author sere
 *
 */
public class ServerOnReaderAndWriterForMultiCli {
	
	static final int PORT = 7777;
	static final String IP = "localhost";
	static AsynchronousChannelGroup threadGroup = null;
	static ExecutorService executorService = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
	
	public static void main(String[] args) {
		
		try {
			threadGroup = AsynchronousChannelGroup.withCachedThreadPool(executorService, 5);
			//threadGroup = AsynchronousChannelGroup.withFixedThreadPool(6, Executors.defaultThreadFactory());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try(AsynchronousServerSocketChannel serverSockChannel = AsynchronousServerSocketChannel.open(threadGroup)){
			//服务端的通道支持两种选项SO_RCVBUF和SOREUSEADDR，一般无需显示设置，使用默认即可
			//在面向流的通道中，此选项表示在前一个连接处于TIME_WAIT状态时，下一个通道是否可以重用的通道地址
			serverSockChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			//设置通道接收的字节大小
			serverSockChannel.setOption(StandardSocketOptions.SO_RCVBUF, 8*1024);
			
			serverSockChannel.bind(new InetSocketAddress(IP, PORT));
			
			System.out.println("Waiting for connections...");
			serverSockChannel.accept(serverSockChannel, new Acceptor());
			
			threadGroup.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	 
}
