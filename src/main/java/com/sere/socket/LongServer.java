package com.sere.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LongServer {

	//private static final int THREADPOOLSIZE = 5;  
	public static final String END_DLIMITER = "\n";   //标记报文结束
	
	public static final String QUIT = "bye";
	
	public static void main(String[] args) {
		System.out.println("------服务端启动了-------");
		ServerSocket server = null;
		Socket socket = null;
		
		boolean isStart = true;
		try {
			server = new ServerSocket(18097);
			
			Executor service = Executors.newCachedThreadPool(); 
			
			
			while (isStart) {
				socket = server.accept(); // 阻塞链接
				socket.setKeepAlive(true); //开启保持活动状态的套接字  防止客户端挂掉还保持失效的链接
				service.execute(new LServerThread(socket));
			}
			
		} catch (IOException e) {
			try {
				isStart = false;
				server.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			
		}

	}
	
	
}

/**
 * 服务端多线程处理
 * @author sere
 *
 */
class LServerThread implements Runnable{
	
	private Socket socket = null;
	
	public LServerThread(Socket socket){
		this.socket = socket;
	}
	
	@Override
	public void run() {
		execute();
	}
	
	private void execute() {
		InputStream is = null;
		OutputStream os = null;
		try{
			is = socket.getInputStream(); // 获取客户端发送的数据
			os = socket.getOutputStream(); // 返回一个SocketOutputStream
			while(true){ //保持长连接
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8")); // 将客户端发送的数据放入缓存
				
				//读取数据
				StringBuilder sb = new StringBuilder();
				String line = reader.readLine();
				System.out.println("服务端输出客户端的信息：" + line);
				
				/**
				 * 如果是长连接一次只能读取一行  line = br.readLine();
				 * 若要全部读取完再处理，适合大数据量的短连接，需要客户端发送完数据关闭socket
				 */
				/*while((line = reader.readLine()) != null){ //阻塞知道读取完毕退出
			      	//System.out.println("s.line:>"+line);
			      	System.out.println("服务端输出客户端的信息：" + line);
			      	sb.append(line);
				}*/
				
				//TODO 做特定的业务逻辑
				PrintWriter pw = new PrintWriter(new OutputStreamWriter(os),true); 
				pw.println(line);
				//pw.write(sb.toString());
				//pw.flush();
				//pw.write(LongServer.END_DLIMITER);
				//pw.flush();
				if(line.endsWith(LongServer.QUIT)) break;
				
				// 释放资源
				//pw.close();  //流的关闭会关闭socket 通道，长连接必须等到链接失效再关闭
				//reader.close(); //流的关闭会关闭socket 通道，长连接必须等到链接失效再关闭
				
			}
			os.close();
			is.close();
			if(!socket.isClosed())
				socket.close();
		}catch(Exception e){
			e.printStackTrace();
			try {
				if(os != null)
					os.close();
				if(is != null){
					is.close();
				}
				if(!socket.isClosed()){
					//socket.shutdownOutput();
					// out.close(); //SocketOutputStream 的close()方法 会关闭socket
					// 程序结束 关闭套接字
					socket.close();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	
	
	//1.socket 的输入输出流任意一个关闭，则socket都不可再用了，所以要关闭就一起关闭了。  
    //2.socket 流的读是阻塞的，A不要输入流关闭前时，要考虑B端的输出流是否还需要写。否则，B端一直等待A端接收，
	//而A端却接收不了，B一直阻塞,在长连接中尤其要注意，注意流的结束标志  
    //3.io 流最后一定要关闭，不然会一直占用内存，可能程序会崩溃。文件输出也可能没有任何信息  
    //4.字符输出推荐使用printWriter 流,它也可以直接对文件操作，它有一个参数，设置为true 可以自动刷新，强制从缓冲中写出数据  
    //5.缓冲流 都有 bufw.newLine();方法，添加换行  
    //6.输入流 是指从什么地方读取/输出流是指输出到什么地方  
    //7.OutputStreamWriter和InputStreamReader是转换流,把字节流转换成字符流  
    //8.Scanner 取得输入的依据是空格符,包括空格键,Tab键和Enter键,任意一个按下就会返回下一个输入，如果需要包括空格之类的则用bufferreader来获取输入  
	private String readCli(BufferedReader reader) throws IOException {
		StringBuilder result = new StringBuilder();
	  
		String line = null;
		
		while((line = reader.readLine()) != null){ //阻塞知道读取完毕退出
	      	System.out.println("s.line:>"+line);
	      	result.append(line);
	      	if(line.endsWith(LongServer.END_DLIMITER)) break;
		}
      
		return result.toString();
	}
}
