import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 客户端
 *
 */
public class Cli {
	public static void main(String args[]) {
		try {
			// 创建socket对象，指定服务器的ip地址，和服务器监听的端口号
			// 客户端在new的时候，就发出了连接请求，服务器端就会进行处理，如果服务器端没有开启服务，那么
			
			//
			Socket s1 = new Socket();
			s1.connect(new InetSocketAddress("localhost", 18888));
			System.out.println(s1.getSendBufferSize() + "----" + s1.getReceiveBufferSize());
			
			if (!s1.isClosed())
				s1.close();
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