import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * 
 * 客户端直接通过浏览器 http://localhost:18096/访问
 * @author sere
 *
 */
public class AioServ {
	
	static int PORT = 18096;
	static int BUFFER_SIZE = 1024;
	static String CHARSET = "utf-8";
	static CharsetDecoder decoder = Charset.forName(CHARSET).newDecoder(); //解码
	
	AsynchronousServerSocketChannel serverchannel;
	
	public AioServ(){
		
	}
	
	private void listen() throws IOException{
		//打开一个服务通道 
		//绑定服务端口
		this.serverchannel = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(PORT), 100);
		this.serverchannel.accept(this,new AcceptHandler());
		//
		Thread t = new Thread(new Runnable() {  
            @Override  
            public void run() {  
                while (true) {  
                    System.out.println("运行中...");  
                    try {  
                        Thread.sleep(2000);  
                    } catch (InterruptedException e) {  
                        e.printStackTrace();  
                    }  
                }  
            }  
        });  
        t.start();  	
	}
	
	private class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AioServ>{

		@Override
		public void completed(AsynchronousSocketChannel result, AioServ attachment) {
			try {
				System.out.println("远程地址："+ result.getRemoteAddress());
				result.setOption(StandardSocketOptions.TCP_NODELAY, true);
				result.setOption(StandardSocketOptions.SO_SNDBUF, 1024);
				result.setOption(StandardSocketOptions.SO_RCVBUF, 1024);
				
				if(result.isOpen()){
					final ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
					buf.clear();
					result.read(buf,result,new ReadHandler(buf));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				attachment.serverchannel.accept(attachment,this);  //监听新的链接
			}
		}
		

		@Override
		public void failed(Throwable exc, AioServ attachment) {
			try{
				exc.printStackTrace();
			}finally{
				attachment.serverchannel.accept(attachment,this); //监听新的链接
			}
		}
		
	}
	
	private class ReadHandler implements CompletionHandler<Integer,AsynchronousSocketChannel>{
		
		private ByteBuffer buf ;
		
		public ReadHandler(ByteBuffer buf){
			this.buf = buf;
		}

		@Override
		public void completed(Integer result, AsynchronousSocketChannel attachment) {
			try{
				if(result < 0){ // 客户端关闭了链接
					attachment.close();
				}else if(result == 0){
					System.out.println("处理空数据！");
				}else{ 
					//读取请求处理客户端的数据
					buf.flip();
					ByteBuffer dump = buf.duplicate();
					CharBuffer charBuf = AioServ.decoder.decode(dump);
					System.out.println("请求数据为："+charBuf.toString());
					
					//响应操作
					buf.clear();
					String res = "HTTP/1.1 200 OK \r\n\n helloworld";
					buf = ByteBuffer.wrap(res.getBytes());
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					attachment.write(buf,attachment,new WriteHandler(buf)); //Response 响应
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}

		@Override
		public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
			exc.printStackTrace();
			AioServ.close(attachment);
		}
		
	}
	
	public class WriteHandler implements CompletionHandler<Integer, AsynchronousSocketChannel>{
		private ByteBuffer buf ;
		
		public WriteHandler(ByteBuffer buf){
			this.buf = buf;
		}
		
		@Override
		public void completed(Integer result, AsynchronousSocketChannel attachment) {
			buf.clear();
			AioServ.close(attachment);
		}

		@Override
		public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
			exc.printStackTrace();
			AioServ.close(attachment);
		}
		
	}
	
	public static void close(AsynchronousSocketChannel channel){
		try {
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		System.out.println("服务启动中......");
		try {
			AioServ serv = new AioServ();
			serv.listen();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}