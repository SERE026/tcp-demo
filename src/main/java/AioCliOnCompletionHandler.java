import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * 基于CompletionHandler实现NIO2的客户端
 * @author sere
 *
 */
public class AioCliOnCompletionHandler {
	
	static int PORT = 18099;
	static String HOST = "127.0.0.1";
	static int BUFFER_SIZE = 1024;
	static String CHARSET = "utf-8";
	static CharsetDecoder decoder = Charset.forName(CHARSET).newDecoder(); //解码
	
	ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
	
	public void start(){
		try (AsynchronousSocketChannel channel = AsynchronousSocketChannel.open()){
			if(channel.isOpen()){
				channel.setOption(StandardSocketOptions.SO_RCVBUF, 128*1024);
				channel.setOption(StandardSocketOptions.SO_SNDBUF, 128*1024);
				channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
				
				channel.connect(new InetSocketAddress(HOST, PORT), null, new CompletionHandler<Void, Void>() {

					@Override
					public void completed(Void result, Void attachment) {
						// TODO Auto-generated method stub
						try {
							System.out.println("Successfully connectioned at:"+channel.getRemoteAddress().toString());
							
							//向服务端发送数据
							channel.write(ByteBuffer.wrap("Hello Server".getBytes())).get();
							
							//读取服务端数据
							while(channel.read(buf).get() != -1){
								buf.flip();
								ByteBuffer dumpBuf = buf.duplicate();
								CharBuffer charBuf = Charset.defaultCharset().decode(dumpBuf);
								
								System.out.println("S recived c data:>"+charBuf.toString());
								if(buf.hasRemaining()){
									buf.compact();
								}
								buf.clear();
								
								int r = new Random().nextInt(1000);
								if(r == 50){
									break;
								}else{
									channel.write(ByteBuffer.wrap("random number is ".concat(String.valueOf(r)).getBytes()));
								}
							}
							
						} catch (IOException e) {
							e.printStackTrace();
						} catch (InterruptedException | ExecutionException e) {
							e.printStackTrace();
						} finally{
							try {
								channel.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}

					@Override
					public void failed(Throwable exc, Void attachment) {
						exc.printStackTrace();
						
					}
				});
			}
			
			
			//主要是阻塞作用，因为AIO是异步的，所以此处不阻塞的话，主线程很快执行完毕，并会关闭通道
            System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}
	
	public static void main(String[] args) {
		AioCliOnCompletionHandler serv = new AioCliOnCompletionHandler();
		serv.start();
	}
}
