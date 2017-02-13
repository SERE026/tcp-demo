import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 客户端
 *
 */
public class NIOCli {
	public static void main(String args[]) {
		
		String addr = "localhost";
		int port = 19000;
		
		byte[] data = "nio client test send".getBytes();
		
		SocketChannel channel;
		try {
			//创建一个socket 通道
			channel = SocketChannel.open();
			channel.configureBlocking(false);  //设置为非阻塞
			
			if(!channel.connect(new InetSocketAddress(addr, port))){ //如果没有连接上
				// 轮询连接状态  ,直到完成连接
				while(!channel.finishConnect()){
					//在等待连接的时间里，可以执行其他任务，以充分发挥非阻塞IO的异步特性  
	                //这里为了演示该方法的使用，只是一直打印"."  
	                System.out.print("."); 
				}
				
			}
			
			System.out.println("-----------------已经连接上了--------------------");
			//分别实例化用来读写的缓冲区  
	        ByteBuffer writeBuf = ByteBuffer.wrap(data);  
	        ByteBuffer readBuf = ByteBuffer.allocate(data.length);
	        
	        //接收的总字节数
	        int totalByteRcvd = 0;
	        
	        //每次调用read() 方法接收的字节数
	        int bytesRcvd;
	        
	        //读取数据
	        while(totalByteRcvd < data.length){
	        	
	        	//如果通道中写 缓冲区还有剩余的字节，则继续将数据写入信道
	        	if(writeBuf.hasRemaining()){
	        		channel.write(writeBuf);
	        	}
	        	
	        	//如果read() 接收到-1 ，表明服务端关闭
	        	if( (bytesRcvd = channel.read(readBuf)) == -1){
	        		throw new SocketException("Connection closed!");
	        		//System.out.println("Connection finish!");
	        	}
	        	
	        	 //计算接收到的总字节数  
	            totalByteRcvd += bytesRcvd;  
	            //在等待通信完成的过程中，程序可以执行其他任务，以体现非阻塞IO的异步特性  
	            //TODO 这里为了演示该方法的使用，同样只是一直打印"."  
	            System.out.print(".");   
	        }
	        //打印出接收到的数据  
	        System.out.println("Received: " +  new String(readBuf.array(), 0, totalByteRcvd));  
	        //关闭信道  
	        channel.close();  
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void sleep() {
		try {
			Thread.sleep(1000*50);
		} catch (Exception e) {
		}
	}
}