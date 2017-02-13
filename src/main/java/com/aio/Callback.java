package com.aio;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * 顶层回调接口
 * @author sere
 *
 */
public interface Callback extends CompletionHandler<Integer, AsynchronousSocketChannel>{
	
	@Override
	void completed(Integer result, AsynchronousSocketChannel attachment);
	
	@Override
	void failed(Throwable exc, AsynchronousSocketChannel attachment);
}
