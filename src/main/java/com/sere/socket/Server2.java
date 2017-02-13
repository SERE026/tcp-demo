package com.sere.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Server2 {

	//private static final int THREADPOOLSIZE = 5;  
	
	public static void main(String[] args) {
		System.out.println("------服务端启动了-------");
		ServerSocket server = null;
		Socket socket = null;
		try {
			server = new ServerSocket(18094);
			
			Executor service = Executors.newCachedThreadPool(); 
			
			boolean flag = true;
			while (flag) {
				socket = server.accept(); // 阻塞链接
				service.execute(new ServerThread(socket));
			}
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
}

/**
 * 服务端多线程处理
 * @author sere
 *
 */
class ServerThread implements Runnable{
	
	private Socket socket = null;
	
	public ServerThread(Socket socket){
		this.socket = socket;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void execute() throws IOException{
		
		InputStream is = socket.getInputStream(); // 获取客户端发送的数据
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8")); // 将客户端发送的数据放入缓存
		
		StringBuilder sb = new StringBuilder();
		String str = "";
		while ((str = reader.readLine()) != null) {
			sb.append(str);
		}
		socket.shutdownInput();
		System.out.println("服务端输出客户端的信息：" + sb);
		
		OutputStream out = socket.getOutputStream(); // 返回一个SocketOutputStream
		
		sb.append("\n");
		out.write(sb.toString().getBytes("UTF-8"));
		out.flush();
		// 释放资源
		reader.close();
		if(!socket.isClosed()){
			//socket.shutdownOutput();
			// out.close(); //SocketOutputStream 的close()方法 会关闭socket
			
			// 程序结束 关闭套接字
			socket.close();
		}
	}
}
