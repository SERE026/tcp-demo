package com.sere.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static void main(String[] args) {
		System.out.println("------服务端启动了-------");
		ServerSocket server = null;
		Socket socket = null;
		try {
			server = new ServerSocket(18094);
			boolean flag = true;
			while (flag) {
				socket = server.accept(); // 阻塞链接
				
				try {
					InputStream is = socket.getInputStream(); // 获取客户端发送的数据

					BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8")); // 将客户端发送的数据放入缓存

					String str = "";
					while ((str = reader.readLine()) != null) {
						String result = "服务端处理成功！\n";
						if (str.equalsIgnoreCase("shutdown")) {  //接收到shutdown 指令，关闭服务器
							result = str + "\n";
							break;
						}
						// socket.shutdownInput();
						System.out.println("服务端输出客户端的信息：" + str);

						OutputStream out = socket.getOutputStream(); // 返回一个SocketOutputStream

						out.write(result.getBytes("UTF-8"));
						out.flush();
						//socket.shutdownOutput();
						// out.close(); //SocketOutputStream 的close()方法 会关闭socket
						 
					}

					// 释放资源
					reader.close();
					// 程序结束 关闭套接字
					socket.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			server.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
