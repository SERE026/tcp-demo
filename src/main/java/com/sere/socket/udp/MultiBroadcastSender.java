package com.sere.socket.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.net.SocketException;

public class MultiBroadcastSender {

	public static void main(String[] args) throws IOException {
		byte[] msg = new String("connect test successfully!!!").getBytes();  
		
		MulticastSocket ms = new MulticastSocket();
		
		/* 
         * 在Java UDP中单播与广播的代码是相同的,要实现具有广播功能的程序只需要使用广播地址即可, 例如：这里使用了本地的广播地址 
         */ 
		InetAddress inetAddr = InetAddress.getByName("224.0.0.5");
		
		//SocketAddress sockAddr = new InetSocketAddress(inetAddr, 18095);
		
		DatagramPacket packet = new DatagramPacket(msg, msg.length, inetAddr,18096);
		
		ms.send(packet);
		ms.close();
	}
}
