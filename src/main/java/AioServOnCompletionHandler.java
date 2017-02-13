import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 基于匿名内部类CompletionHandler实现NIO2的服务端
 * @author sere
 *
 */
public class AioServOnCompletionHandler {
	
	static int PORT = 18099;
	static String HOST = "127.0.0.1";
	static int BUFFER_SIZE = 1024;
	static String CHARSET = "utf-8";
	static CharsetDecoder decoder = Charset.forName(CHARSET).newDecoder(); //解码
	
	static ExecutorService taskExecutorService = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
	
	public void start(){
		try (AsynchronousServerSocketChannel serverchannel = AsynchronousServerSocketChannel.open()){
			
			
			if(serverchannel.isOpen()){
				serverchannel.setOption(StandardSocketOptions.SO_RCVBUF, 4*1024);
				serverchannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
				serverchannel.bind(new InetSocketAddress(HOST,PORT), 100);
				System.out.println("waiting for connection.......");
				
				serverchannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
					final ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
					@Override
					public void completed(AsynchronousSocketChannel result, Void attachment) {
						//注意接收一个连接之后，紧接着可以接收下一个连接，所以必须再次调用accept方法
						serverchannel.accept(null,this);
						try{
							while(result.read(buf).get() != -1){
								
								//读取数据
								buf.flip();
								// Java NIO2或者Java AIO报： java.util.concurrent.ExecutionException: java.io.IOException: 指定的网络名不再可用。
		                        // 此处要注意，千万不能直接操作buffer，否则客户端会阻塞并报错，“java.util.concurrent.ExecutionException: java.io.IOException: 指定的网络名不再可用。”
								ByteBuffer dumpbuf = buf.duplicate();
								CharBuffer charbuf = Charset.defaultCharset().decode(dumpbuf);
								System.out.println("S recived c data:>"+charbuf.toString());
								
								//响应客户端数据
								result.write(buf).get();
								if(buf.hasRemaining()){
									buf.compact();
								}else{
									buf.clear();
								}
							}
						}catch(Exception e){
							
						}
						
					}

					@Override
					public void failed(Throwable exc, Void attachment) {
						serverchannel.accept(null,this);
						exc.printStackTrace();
					}
				});
				//主要是阻塞作用，因为AIO是异步的，所以此处不阻塞的话，主线程很快执行完毕，并会关闭通道
                System.in.read();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}
	
	public static void main(String[] args) {
		AioServOnCompletionHandler serv = new AioServOnCompletionHandler();
		serv.start();
	}
}
