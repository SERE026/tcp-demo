package sere.demo2.time;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeClientHandler extends ChannelInboundHandlerAdapter {
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		//super.channelRead(ctx, msg);
		/*ByteBuf m = (ByteBuf) msg;
		try{
			long currentTime = (m.readUnsignedInt() - 2208988800L) * 1000L;
			System.out.print(new Date(currentTime));
			ctx.close();
		}catch (Exception e) {
			// TODO: handle exception
		}finally{
			m.release();
		}*/
		
		UnixTime m = (UnixTime) msg;
		System.out.println(m);
		ctx.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		//super.exceptionCaught(ctx, cause);
		cause.printStackTrace();
		ctx.close();
	}
}
