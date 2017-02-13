import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

/**
 * 
 * 
 *
 */
public class NIOServ {
	
	//缓冲区的长度  
    private static final int BUFSIZE = 256;   
    //select方法等待信道准备好的最长时间  
    private static final int TIMEOUT = 3000;
	
	public static void main(String args[]) {
		
		if(args.length == 0){
			args = new String[]{"19000","19001","19002"};
		}
		
		try {
			//创建选择器
			Selector selector = Selector.open();
			
			for(String arg : args){
				//实例化信道
				ServerSocketChannel serverChannel = ServerSocketChannel.open();
				//将信道绑定到指定端口
				serverChannel.socket().bind(new InetSocketAddress(Integer.parseInt(arg)));
				//设置为非阻塞模式
				serverChannel.configureBlocking(false);
				//将选择器注册到各个信道
				serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			}
			
			//创建一个实现了协议接口的对象  
			TCPProtocol protocol = new EchoSelectorProtocol(BUFSIZE);
			
			//不断轮询select方法，获取准备好的信道所关联的Key集 
			while(true){
				if(selector.select(TIMEOUT) == 0 ){
					//在等待通信完成的过程中，程序可以执行其他任务，以体现非阻塞IO的异步特性  
		            //TODO 这里为了演示该方法的使用，同样只是一直打印"."  
		            System.out.print("."); 
				}
				//获取准备好的所关联的key集合
				Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
				
				//获取每个键值
				while(keys.hasNext()){
					SelectionKey key = keys.next();
					
					//如果信道感兴趣的I/O操作为accept
					if(key.isAcceptable()){
						protocol.accept(key);
					}
					
					//如果感兴趣的I/O 操作为read
					if(key.isReadable()){
						protocol.read(key);
					}
					
					//如果该值有效且感兴趣的I/O 操作为write
					if(key.isValid() && key.isWritable()){
						protocol.write(key);
					}
					
					//移除当前的key
					keys.remove();
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static void sleep() {
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}
	}

}
