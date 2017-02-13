package com.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class Writer implements WriterCallback{
	
	private ByteBuffer buf;
	
	public Writer(ByteBuffer buf){
		this.buf = buf;
	}

	@Override
	public void completed(Integer result, AsynchronousSocketChannel attachment) {
		System.out.println("Message write successfully size :"+result);
		System.out.println("Writer name: "+ Thread.currentThread().getName());
		
		buf.clear();
		attachment.read(buf,attachment,new Reader(buf));
	}

	@Override
	public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
		exc.printStackTrace();
	}
	
}
