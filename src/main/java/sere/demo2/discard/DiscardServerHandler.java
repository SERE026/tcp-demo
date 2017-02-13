package sere.demo2.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		//super.channelRead(ctx, msg);
		
		//ByteBuf in = (ByteBuf) msg;
		
		ctx.write(msg);
		ctx.flush();
		
		//((ByteBuf) msg).release(); // (3)
		/*try{
			while(in.isReadable()){
				System.out.print((char)in.readByte());
				System.out.flush();
			}
		}finally{
			ReferenceCountUtil.release(msg);
		}*/
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		//super.exceptionCaught(ctx, cause);
		cause.printStackTrace();
		ctx.close();
	}

}
