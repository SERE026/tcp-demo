import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Description:基于Future的NIO2服务端实现，此时的服务端还无法实现多客户端并发，如果有多个客户端并发连接该服务端的话，
 * 客户端会出现阻塞，待前一个客户端处理完毕，服务端才会接受下一个客户端的连接并处
 * http://mobile.www.cnblogs.com/personnel/p/4583283.html
 * @author sere
 *
 */
public class AioServOnFuture {
	
	static int PORT = 18097;
	static String HOST = "127.0.0.1";
	static int BUFFER_SIZE = 1024;
	static String CHARSET = "utf-8";
	static CharsetDecoder decoder = Charset.forName(CHARSET).newDecoder(); //解码
	
	AsynchronousServerSocketChannel serverchannel;
	
	public void start(){
		try {
			ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
			serverchannel = AsynchronousServerSocketChannel.open();
			if(serverchannel.isOpen()){
				serverchannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
				serverchannel.bind(new InetSocketAddress(HOST,PORT), 100);
				System.out.println("waiting for connection.......");
				while(true){
					Future<AsynchronousSocketChannel> channelFuture = serverchannel.accept(); 
					AsynchronousSocketChannel socketChannel = channelFuture.get();
					System.out.println("incoming connection from :"+socketChannel.getRemoteAddress().toString());
					
					while(socketChannel.read(buf).get() != -1){
						
						//读取数据
						buf.flip();
						// Java NIO2或者Java AIO报： java.util.concurrent.ExecutionException: java.io.IOException: 指定的网络名不再可用。
                        // 此处要注意，千万不能直接操作buffer，否则客户端会阻塞并报错，“java.util.concurrent.ExecutionException: java.io.IOException: 指定的网络名不再可用。”
						ByteBuffer dumpbuf = buf.duplicate();
						CharBuffer charbuf = Charset.defaultCharset().decode(dumpbuf);
						System.out.println("S recived c data:>"+charbuf.toString());
						
						//响应客户端数据
						Thread.sleep(100);
						socketChannel.write(buf).get();
						if(buf.hasRemaining()){
							buf.compact();
						}else{
							buf.clear();
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		AioServOnFuture serv = new AioServOnFuture();
		serv.start();
	}
}
