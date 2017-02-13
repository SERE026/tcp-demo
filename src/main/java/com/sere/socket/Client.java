package com.sere.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Socket socket = null;
		try {
			socket = new Socket("localhost", 18094);
			System.out.println("------客户端连接上了-------");
			// socket.connect(new InetSocketAddress(), 1000);
			OutputStream out = socket.getOutputStream();

			Scanner sc = new Scanner(System.in);

			String data = sc.next() + "\n";
			System.out.println(data);
			out.write(data.getBytes("UTF-8"));
			out.flush();
			socket.shutdownOutput();

			InputStream is = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String str = "";
			while ((str = reader.readLine()) != null) {
				System.out.println("客户端接收服务端的数据：" + str);
			}

			reader.close();
			// is.close();
			if(!socket.isClosed()){
				//socket.shutdownInput();
				socket.close();
				// out.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
