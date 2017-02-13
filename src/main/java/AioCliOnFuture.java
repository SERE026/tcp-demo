import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Description:基于Future的NIO2服务端实现，此时的服务端还无法实现多客户端并发，如果有多个客户端并发连接该服务端的话，
 * 客户端会出现阻塞，待前一个客户端处理完毕，服务端才会接受下一个客户端的连接并处
 * @author sere
 *
 */
public class AioCliOnFuture {
	
	static int PORT = 18097;
	static String HOST = "127.0.0.1";
	static int BUFFER_SIZE = 1024;
	static String CHARSET = "utf-8";
	static CharsetDecoder decoder = Charset.forName(CHARSET).newDecoder(); //解码
	
	AsynchronousSocketChannel channel;
	
	public void start(){
		try {
			ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
			channel = AsynchronousSocketChannel.open();
			if(channel.isOpen()){
				//设置一些选项，非必选项，可使用默认设置
				channel.setOption(StandardSocketOptions.SO_RCVBUF, 128 * 1024);
				channel.setOption(StandardSocketOptions.SO_SNDBUF, 128 * 1024);
				channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
				
				Void avoid = channel.connect(new InetSocketAddress(HOST,PORT)).get();
				
				if(avoid == null){ //返回null表示链接成功
					Integer msgLen = channel.write(ByteBuffer.wrap("Hello Server".getBytes())).get();
					System.out.println("消息长度："+msgLen);
					
					while(channel.read(buf).get() != -1){
						buf.flip();
						CharBuffer charBuf = Charset.defaultCharset().decode(buf);
						System.out.println("C recived S data:>"+charBuf.toString());
						
						if(buf.hasRemaining()){
							buf.compact();
						}else{
							buf.clear();
						}
						int r = new Random().nextInt(1000);
						if(r == 50){
							break;
						}else{
							// Java NIO2或者Java AIO报： Exception in thread "main" java.nio.channels.WritePendingException
                            // 此处注意，如果在频繁调用write()的时候，在上一个操作没有写完的情况下，调用write会触发WritePendingException异常，
                            // 所以此处最好在调用write()之后调用get()以便明确等到有返回结果
							Thread.sleep(100);
							channel.write(ByteBuffer.wrap("Random number is ".concat(String.valueOf(r)).getBytes())).get();
						}
					}
				}
				
				System.out.println("waiting for connection.......");
				
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
		AioCliOnFuture cli = new AioCliOnFuture();
		cli.start();
	}
}
