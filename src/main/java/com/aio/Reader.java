package com.aio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;

/**
 * 负责服务端读消息
 * @author sere
 *
 */
public class Reader implements ReaderCallback {
	
	private ByteBuffer buf;
	
	public Reader(ByteBuffer buf){
		this.buf = buf;
	}

	@Override
	public void completed(Integer result, AsynchronousSocketChannel attachment) {
		System.out.println("Reader name: "+ Thread.currentThread().getName());
		buf.flip();
		
		System.out.println("Message size :"+result);
		if(result != null && result < 0){
			try {
				attachment.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		try{
			SocketAddress local = attachment.getLocalAddress();
			SocketAddress remote = attachment.getRemoteAddress();
			
			System.out.println("localAddress:"+local.toString());
			System.out.println("remoteAddress:"+remote.toString());
			
			//响应数据
			attachment.write(buf,attachment,new Writer(buf));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//读取数据
		ByteBuffer dumpBuf = buf.duplicate();
		CharBuffer decode = Charset.defaultCharset().decode(dumpBuf);
		System.out.println("Recive message from client:"+decode.toString());
	}

	@Override
	public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
		exc.printStackTrace();
	}

}
