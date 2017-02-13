package com.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;


public class Acceptor implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {  
    
    private final ByteBuffer buffer = ByteBuffer.allocate(1024);  
      
    public Acceptor(){  
        System.out.println("an acceptor has created.");  
    }  
      
    public void completed(final AsynchronousSocketChannel channel, AsynchronousServerSocketChannel serverChannel) {  
        System.out.println(String.format("write: name: %s", Thread.currentThread().getName()));  
        channel.read(buffer, channel, new Reader(buffer));  
        serverChannel.accept(serverChannel, new Acceptor());  
    }  
      
    public void failed(Throwable exception, AsynchronousServerSocketChannel serverChannel) {  
        throw new RuntimeException(exception);  
    }  
} 
