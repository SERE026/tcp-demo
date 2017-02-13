package com.sere.socket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 服务器断的工作就是在指定的端口上监听
 * <li>建立连接</li>
 * <li>打开输出流</li>
 * <li>封装输出流</li>
 * <li>向客户端发送数据</li>
 * <li>关闭打开的输出流</li>
 * <li>关闭打开的socket对象</li>
 * 
 */
public class TestServer2 {
	public static void main(String args[]) {
		try {
			// 创建一个socket对象
			ServerSocket s = new ServerSocket(8888);
			
			// socket对象调用accept方法，等待连接请求
			Socket s1 = s.accept();
			System.out.println("默认-->发送缓冲区大小："+s1.getSendBufferSize() +"----接收缓冲区大小："+s1.getReceiveBufferSize());
			s1.setSendBufferSize(10);   //发送缓冲区  如果超出 需要等待revQ 读取数据之后方可发送
			s1.setReceiveBufferSize(3);  //接收缓冲区，如果超出，需要等待sendQ 发送之后才可读取，否则 一直阻塞
			System.out.println("修改后-->发送缓冲区大小："+s1.getSendBufferSize() +"----接收缓冲区大小："+s1.getReceiveBufferSize());
			// 打开输出流
			OutputStream os = s1.getOutputStream();
			// 封装输出流
			BufferedOutputStream bos = new BufferedOutputStream(os);
			
			// 打开输入流
			InputStream is = s1.getInputStream();
			// 封装输入流
			BufferedInputStream bis = new BufferedInputStream(is);
			
			// 读取键盘输入流
			InputStreamReader isr = new InputStreamReader(System.in);
			// 封装键盘输入流
			BufferedReader br = new BufferedReader(isr);

			
			String info;
			while (true) {
				s1.setReceiveBufferSize(3);
				StringBuilder sb = new StringBuilder();
				info = "";
				if(bis.available() == 0) continue;
				System.out.println("报文长度："+bis.available());
				
				byte[] buf = new byte[5]; 
				
				// while((b = bis.read(buf)) != -1) {  // -1 是指文件末尾，socket流关闭， 
				while(bis.available() > 5){
					bis.read(buf);
	            	//br.mark(-1) //程序退出
	                info = new String(buf,"UTF-8");
	                
	                sb.append(info);
	                Arrays.fill(buf, (byte)0);
	                System.out.println("in:"+info);
	            } 
				
				//处理不足 5的 剩余数据
				int remain = bis.available();   
		        byte[] last = new byte[remain];  
		        bis.read(last); 
		        info = new String(last,"UTF-8");
		        sb.append(info );
		        
	              
	            info = sb.toString();
	            
				// 打印接受的信息
				System.out.println("客户端说: " + info);
				// 如果发现接受的信息为：bye，那么就结束对话
				if (info.equals("bye"))
					break;
				// 读取键盘的输入流
				//info = br.readLine();
				// 写入到网络连接的另一边，即客户端
				bos.write(info.getBytes("UTF-8"));
				bos.flush();
				// 如果服务器自己说：bye，也是结束对话
				if (info.equals("bye"))
					break;
			}
			// 关闭输入流
			if(bis != null)
				bis.close();
			// 关闭输出流
			if(bos != null)
				bos.close();
			// 关闭socket对象
			if(!s1.isClosed())
				s1.close();
			s.close();
		} catch (SocketException e) {
			e.printStackTrace();
			System.out.println("网络连接异常，程序退出!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
