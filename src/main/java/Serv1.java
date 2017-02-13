import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * 非LISTEN 状态的Recv-Q 和 send-Q  
 * 查看命令：ss -t -a -p |sed -n -e 1,2p -e /port/p
 * 场景 ： 客户端给服务端发送数据 ，服务端不取数据
 * 
 *  [root@dev ~]#ss -t -a -p |sed -n -e 1,2p -e /apc-necmp/p
	State      Recv-Q Send-Q      Local Address:Port          Peer Address:Port   
	LISTEN     0      3                      :::apc-necmp                 :::*        users:(("java",11946,5))
	ESTAB      0      0        ::ffff:127.0.0.1:apc-necmp   ::ffff:127.0.0.1:58269    users:(("java",11946,6))
	ESTAB      0      0        ::ffff:127.0.0.1:58269     ::ffff:127.0.0.1:apc-necmp  users:(("java",11960,5))
    [root@dev ~]#ss -t -a -p |sed -n -e 1,2p -e /apc-necmp/p
	State      Recv-Q Send-Q      Local Address:Port          Peer Address:Port   
	LISTEN     0      3                      :::apc-necmp                 :::*        users:(("java",11946,5))
	ESTAB      1800   0        ::ffff:127.0.0.1:apc-necmp   ::ffff:127.0.0.1:58269    users:(("java",11946,6))
	ESTAB      0      1980     ::ffff:127.0.0.1:58269     ::ffff:127.0.0.1:apc-necmp  users:(("java",11960,5))
	[root@dev ~]#ss -t -a -p |sed -n -e 1,2p -e /apc-necmp/p
	State      Recv-Q Send-Q      Local Address:Port          Peer Address:Port   
	LISTEN     0      3                      :::apc-necmp                 :::*        users:(("java",11946,5))
	ESTAB      1800   0        ::ffff:127.0.0.1:apc-necmp   ::ffff:127.0.0.1:58269    users:(("java",11946,6))
	ESTAB      0      2070     ::ffff:127.0.0.1:58269     ::ffff:127.0.0.1:apc-necmp  users:(("java",11960,5))
 * 
 * 服务端与客户端链接的状态为： ESTABLISHED ，此时 该链接Recv-Q 队列中存储着客户端发送的数据  当达到1800的时候，队列填满，不再增加
 * 客户端与服务端链接的状态为：ESTABLISTED  中Recv-Q 和send-Q 都为零 ，直到 服务端的recv-Q 队列填满，此时数据堆积在客户端 的send-Q
 *
 */
public class Serv1 {
	public static void main(String args[]) {
		try {
			// 创建一个socket对象
			ServerSocket s = new ServerSocket(18888,3);

			// socket对象调用accept方法，等待连接请求
			Socket s1 = s.accept();
			System.out.println("默认-->发送缓冲区大小：" + s1.getSendBufferSize() + "----接收缓冲区大小：" + s1.getReceiveBufferSize());
			// s1.setSendBufferSize(1); // 对socket的缓冲区没有什么影响
			// s1.setReceiveBufferSize(1); //接收缓冲区 对socket的缓冲区没什么影响
			System.out.println("修改后-->发送缓冲区大小："+s1.getSendBufferSize() +"----接收缓冲区大小："+s1.getReceiveBufferSize());
			
			/**
			 * 不取数据，等到 Recv-Q 填满
			 */
			// 一直阻塞 
			while(true){
				sleep();
			}
			
		} catch (SocketException e) {
			e.printStackTrace();
			System.out.println("网络连接异常，程序退出!");
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
