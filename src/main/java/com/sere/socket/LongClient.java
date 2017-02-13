package com.sere.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Scanner;

public class LongClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Socket socket = null;
		try {
			socket = new Socket("localhost", 18097);
			if(socket.isConnected()){
				System.out.println("------客户端连接上了-------");
				OutputStream os = socket.getOutputStream();
				InputStream is = socket.getInputStream();
				
				//获取键盘输入流
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				
				while(true){  //保持长连接
					String data = reader.readLine();
					
					//String data = "cestesateaedddddddddddddddddddddddddddddddddddddddddddddddd";
					
					System.out.println(data);
					
					//向服务端发送数据
					PrintWriter pw = new PrintWriter(new OutputStreamWriter(os),true); 
					pw.println(data);
					//pw.write(data);
					//pw.flush();
					//pw.write(LongServer.END_DLIMITER);   //写入结束标记符 ，服务端与客户端约定
					//pw.flush();
					
					//获取服务端的响应数据
					BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					String sb = readResServer(br);
					
					if(sb.equals(LongServer.QUIT)) break;
					System.out.println("收到服务器响应数据：>"+sb);
				}
				
				//释放资源
				reader.close();
				
				if(os != null)
					os.close();
				if(is != null)
					is.close();
				if(!socket.isClosed())
					socket.close();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static String readResServer(BufferedReader br) throws IOException, UnsupportedEncodingException {
		
		
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();
		sb.append(line);
		
		/**
		 * 如果是长连接一次只能读取一行  line = br.readLine();
		 * 若要全部读取完再处理，适合大数据量的短连接，需要客户端发送完数据关闭socket
		 */
		
		/*while((line = br.readLine()) != null){   
			System.out.println("收到服务器响应数据：>"+line);
			// TODO 处理特定业务逻辑
			sb.append(line);
			if(line.endsWith(LongServer.END_DLIMITER)) break;
		}*/
		return sb.toString();
	}

}
