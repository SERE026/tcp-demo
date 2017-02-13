package com.sere.chat1;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	
	private final String wsUri;
	private static File INDEX = null;
	
	static {
		
		try {
			URL location = HttpRequestHandler.class.getProtectionDomain().getCodeSource().getLocation();
			String path = location.toURI()+"WebsocketChatClient.html";
			path = !path.contains("file:") ? path : path.substring(5);
			INDEX = new File(path);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public HttpRequestHandler(String wsUri) {
		super();
		this.wsUri = wsUri;
	}



	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		// TODO Auto-generated method stub
		if(wsUri.equalsIgnoreCase(request.getUri())){
			ctx.fireChannelRead(request.retain());
		}else{
			if(HttpHeaders.is100ContinueExpected(request)){
				send100Continue(ctx);
			}
			
			RandomAccessFile file = new RandomAccessFile(INDEX, "r");
			HttpResponse response = new DefaultHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);
			response.headers().set(HttpHeaders.Names.CONTENT_TYPE,"text/html; charset=UTF-8");
			boolean keepAlive = HttpHeaders.isKeepAlive(request);
			
			if(keepAlive){
				response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length());
				response.headers().set(HttpHeaders.Names.CONNECTION,HttpHeaders.Values.KEEP_ALIVE);
			}
			
			ctx.write(response);
			
			if(ctx.pipeline().get(SslHandler.class) == null){
				ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
			}else{
				ctx.write(new ChunkedNioFile(file.getChannel()));
			}
			
			ChannelFuture cf = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
			
			if(!keepAlive)
				cf.addListener(ChannelFutureListener.CLOSE);
			
			file.close();
		}
	}



	private void send100Continue(ChannelHandlerContext ctx) {
		// TODO Auto-generated method stub
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
		ctx.writeAndFlush(response);
				
	}



	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		//super.exceptionCaught(ctx, cause);
		cause.printStackTrace();
		ctx.close();
	}

	
}
