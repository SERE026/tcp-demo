package com.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * 基于NIO2 读写模式的客户端
 * @author sere
 *
 */
public class ClientOnReaderAndWriter {
	
	static final int PORT = 7777;
	static final String IP = "localhost";
	
	static ByteBuffer buf = ByteBuffer.allocate(1024);
	
	public static void main(String[] args) {
		
		try(AsynchronousSocketChannel sockChannel = AsynchronousSocketChannel.open()){
			Void avoid = sockChannel.connect(new InetSocketAddress(IP, PORT)).get();
			
			if(sockChannel.isOpen()){
				
				if(avoid == null){
					sockChannel.setOption(StandardSocketOptions.SO_RCVBUF, 8*1024);
					sockChannel.setOption(StandardSocketOptions.SO_SNDBUF, 8*1024);
					sockChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
					
					//向服务端发送消息
					sockChannel.write(ByteBuffer.wrap("Hello Server".getBytes())).get();
					
					//读取服务端响应消息
					while(sockChannel.read(buf).get() != -1){
						buf.flip();
						CharBuffer charBuf = Charset.defaultCharset().decode(buf);
						System.out.println("Recived Server response info :"+charBuf.toString());
						
//                      如果调用的是clear()方法，position将被设回0，limit被设置成capacity的值。换句话说，Buffer被清空了。
//                      Buffer中的数据并未清除，只是这些标记告诉我们可以从哪里开始往Buffer里写数据。
//                      如果Buffer中有一些未读的数据，调用clear()方法，数据将“被遗忘”，意味着不再有任何标记会告诉你哪些数据被读过，哪些还没有。
//                      如果Buffer中仍有未读的数据，且后续还需要这些数据，但是此时想要先先写些数据，那么使用compact()方法。
//                      compact()方法将所有未读的数据拷贝到Buffer起始处。然后将position设到最后一个未读元素正后面。
//                      limit属性依然像clear()方法一样，设置成capacity。现在Buffer准备好写数据了，但是不会覆盖未读的数据。
						if(buf.hasRemaining()){
							buf.compact();
						}else{
							buf.clear();
						}
						
						int r = new Random().nextInt(1000);
						if(r == 50){
							break;
						}else{
							sockChannel.write(ByteBuffer.wrap("Random number is:".concat(String.valueOf(r)).getBytes()));
						}
						
					}
				}
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}
