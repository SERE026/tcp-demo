import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 客户端
 * 验证 非 LISTEN 状态的 Recv-Q 和 Send-Q 
 * 
 */
public class Cli1 {
	public static void main(String args[]) {
		try {
			// 创建socket对象，指定服务器的ip地址，和服务器监听的端口号
			Socket s1 = new Socket("localhost", 18888);
			s1.setSendBufferSize(1);
			System.out.println("修改后-->发送缓冲区大小："+s1.getSendBufferSize() +"----接收缓冲区大小："+s1.getReceiveBufferSize());
			// 打开输出流
			OutputStream os = s1.getOutputStream();
			// 读取键盘输入流
			InputStreamReader isr = new InputStreamReader(System.in);
			// 封装键盘输入流
			BufferedReader br = new BufferedReader(isr);
		     
			// 客户端先读取键盘输入信息
			String info = br.readLine();	
			while(true){
				// 不断的发送数据，查看 Recv-Q 和 send-Q的队列状态
				os.write(info.getBytes("UTF-8"));
				sleep();
				//os.flush();
			}
			// 关闭相应的输入流，输出流，socket对象
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public static void sleep() {
		try {
			Thread.sleep(5000);
		} catch (Exception e) {
		}
	}
	
}
