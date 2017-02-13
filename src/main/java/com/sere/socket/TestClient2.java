package com.sere.socket;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

/**
 * 客户端
 * 
 */
public class TestClient2 {
	public static void main(String args[]) {
		try {
			// 创建socket对象，指定服务器的ip地址，和服务器监听的端口号
			// 客户端在new的时候，就发出了连接请求，服务器端就会进行处理，如果服务器端没有开启服务，那么
			// 这时候就会找不到服务器，并同时抛出异常==》java.net.ConnectException: Connection
			// refused: connect
			Socket s1 = new Socket("localhost", 8888);
			System.out.println(s1.getSendBufferSize() +"----"+s1.getReceiveBufferSize());
			// 打开输出流
			OutputStream os = s1.getOutputStream();
			
			// 打开输入流
			InputStream is = s1.getInputStream();
			
			// 读取键盘输入流
			InputStreamReader isr = new InputStreamReader(System.in);
			// 封装键盘输入流
			BufferedReader br = new BufferedReader(isr);

			String info;
			while (true) {
				// 客户端先读取键盘输入信息
				info = br.readLine();
				// 把他写入到服务器方
				os.write(info.getBytes("UTF-8"));
				os.flush();
				
				// 如果客户端自己说：bye，即结束对话
				if (info.equals("bye"))
					break;
				
				info = "";
				
				// 接受服务器端信息
				byte[] buf = new byte[512];  
				while(is.available() > 512){
					is.read(buf);
	            	//br.mark(-1) //程序退出
	                info += new String(buf,"UTF-8");
	                Arrays.fill(buf, (byte)0);
	                //System.out.println("in:"+info);
	            } 
				
				//处理不足 5的 剩余数据
				int remain = is.available();   
		        byte[] last = new byte[remain];  
		        is.read(last); 
		        info += new String(last,"UTF-8");
				// 打印
				System.out.println("服务端说: " + info);
				if (info.equals("bye"))
					break;
			}
			// 关闭相应的输入流，输出流，socket对象
			br.close();
			if(is != null)
				is.close();
			if(os != null)
				os.close();
			if(!s1.isClosed())
				s1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
