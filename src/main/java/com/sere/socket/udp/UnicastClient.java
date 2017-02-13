package com.sere.socket.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UnicastClient {
	
	public static int MAX_RECEVED = 255;
	
	public static void main(String[] args) throws IOException {
		byte[] msg = new String("connect test successfully!!!").getBytes();  
		
		DatagramSocket ds = new DatagramSocket();
		
		InetAddress inetAddr = InetAddress.getLocalHost();
		
		SocketAddress sockAddr = new InetSocketAddress(inetAddr, 18095);
		
		DatagramPacket packet = new DatagramPacket(msg, msg.length, sockAddr);
		
		ds.send(packet);
		ds.close();
	}
}
