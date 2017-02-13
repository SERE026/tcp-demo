package com.sere.chat1;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebsocketChatServerInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		// TODO Auto-generated method stub
		ChannelPipeline cp = ch.pipeline();
		cp.addLast(new HttpServerCodec());
		cp.addLast(new HttpObjectAggregator(64*1024));
		cp.addLast(new ChunkedWriteHandler());
		cp.addLast(new HttpRequestHandler("/ws"));
		cp.addLast(new WebSocketServerProtocolHandler("/ws"));
		cp.addLast(new TextWebSocketFrameHandler());
		
		
		System.out.println("SimpleChatClent:"+ch.remoteAddress() +"连接上\n");
	}

}
