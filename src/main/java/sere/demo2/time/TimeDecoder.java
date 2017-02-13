package sere.demo2.time;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class TimeDecoder extends ByteToMessageDecoder{

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		// TODO Auto-generated method stub
		if( in.readableBytes() < 4)
			return;
		//out.add(in.readBytes(4)); 
		out.add(new UnixTime(in.readUnsignedInt()));
	}

}
