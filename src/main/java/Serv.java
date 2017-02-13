import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Socket的握手过程
 * send （应用程序buffer）---socket (tcp buffer)--? (系统？网卡？) -----网线--- (系统？网卡？) --socket (tcp buffer)--recv（应用程序buffer）
	socket流缓存，操作系统缓存，网卡，网卡，操作系统缓存，socket缓存
	
 * socket 创建连接时会发送 协议版本、 等数据
 * 握手过程java代码中无体现
 * 具体流程： tcpdump 'port 18888' -i lo -S
 *  16:18:17.871056 IP localhost.46702 > localhost.apc-necmp: Flags [S], seq 837457501, win 43690, options [mss 65495,sackOK,TS val 1724755025 ecr 0,nop,wscale 7], length 0
	02:25:32.759802 IP localhost.apc-necmp > localhost.46702: Flags [S.], seq 3735032304, ack 837457502, win 43690, options [mss 65495,sackOK,TS val 1724755025 ecr 1724755025,nop,wscale 7], length 0
	16:18:17.871095 IP localhost.46702 > localhost.apc-necmp: Flags [.], ack 3735032305, win 342, options [nop,nop,TS val 1724755025 ecr 1724755025], length 0
	16:19:41.556744 IP localhost.46702 > localhost.apc-necmp: Flags [F.], seq 837457502, ack 3735032305, win 342, options [nop,nop,TS val 1724838711 ecr 1724755025], length 0
	16:19:41.557553 IP localhost.apc-necmp > localhost.46702: Flags [.], ack 837457503, win 342, options [nop,nop,TS val 1724838712 ecr 1724838711], length 0
	16:20:05.692014 IP localhost.apc-necmp > localhost.46702: Flags [F.], seq 3735032305, ack 837457503, win 342, options [nop,nop,TS val 1724862846 ecr 1724838711], length 0
	16:20:05.692034 IP localhost.46702 > localhost.apc-necmp: Flags [.], ack 3735032306, win 342, options [nop,nop,TS val 1724862846 ecr 1724862846], length 0
 * 1.客户端 发送 [S] seq -->服务端
 * 2.服务端 响应 [S.] seq ack-->客户端
 * 3.客户端 响应 [.] ack -->服务端
 * 4.客户端主动关闭 发送 [F.] seq ack -->服务端
 * 5.服务端响应 [.] ack -->客户端
 * 6.服务端主动关闭 发送 [F.] seq ack -->客户端
 * 7.客户端响应 [.] ack --> 服务端
 * 
 * 
 * socket 中处于LISTEN状态的send-Q 为 backlog数，即允许客户端的连接数
 * socket 中处于LISTEN状态的recv-Q 为 服务端为调用accept()放到的链接
 */
public class Serv {
	public static void main(String args[]) {
		try {
			// 创建一个socket对象
			ServerSocket s = new ServerSocket(18888);

			// socket对象调用accept方法，等待连接请求
			// 验证recv-Q listen 中的recv-Q是指客户端已经发送连接请求，服务端还未调用accept
			sleep();
			Socket s1 = s.accept();
			System.out.println("默认-->发送缓冲区大小：" + s1.getSendBufferSize() + "----接收缓冲区大小：" + s1.getReceiveBufferSize());
			
			
			// 关闭socket对象
			if (!s1.isClosed())
				s1.close();
			s.close();
		} catch (SocketException e) {
			e.printStackTrace();
			System.out.println("网络连接异常，程序退出!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void sleep() {
		try {
			Thread.sleep(7000);
		} catch (Exception e) {
		}
	}

}