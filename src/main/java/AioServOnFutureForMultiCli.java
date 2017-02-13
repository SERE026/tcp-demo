import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 基于Future实现的可以接受多客户端并发的Java NIO2服务端实现
 * @author sere
 *
 */
public class AioServOnFutureForMultiCli {
	
	static int PORT = 18098;
	static String HOST = "127.0.0.1";
	static int BUFFER_SIZE = 1024;
	static String CHARSET = "utf-8";
	static CharsetDecoder decoder = Charset.forName(CHARSET).newDecoder(); //解码
	
	static ExecutorService taskExecutorService = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
	
	public void start(){
		try (AsynchronousServerSocketChannel serverchannel = AsynchronousServerSocketChannel.open()){
			final ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
			
			if(serverchannel.isOpen()){
				serverchannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
				serverchannel.bind(new InetSocketAddress(HOST,PORT), 100);
				System.out.println("waiting for connection.......");
				while(true){
					Future<AsynchronousSocketChannel> channelFuture = serverchannel.accept(); 
					final AsynchronousSocketChannel socketChannel = channelFuture.get();
					
					Callable<String> worker = new Callable<String>() {

						@Override
						public String call() throws Exception {
							String result = socketChannel.getRemoteAddress().toString();
							System.out.println("incoming connection from :"+result);
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
							
							return result;
						}
					};
					
					taskExecutorService.submit(worker);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			taskExecutorService.shutdown();
			while(!taskExecutorService.isTerminated()){
				
			}
			
		} 
		
	}
	
	public static void main(String[] args) {
		AioServOnFutureForMultiCli serv = new AioServOnFutureForMultiCli();
		serv.start();
	}
}
