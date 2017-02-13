package com.sere.socket.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

public class MultiBroadcastServer {

	public static int MAX_RECEVED = 255;
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		InetAddress inetAddr = InetAddress.getByName("224.0.0.5");
		MulticastSocket ms = new MulticastSocket(18096);
		
		
		DatagramPacket packet = new DatagramPacket(new byte[MAX_RECEVED], MAX_RECEVED);
		
		ms.joinGroup(inetAddr);
		
		while(true){
			ms.receive(packet);
			
			byte[] recvMsg = Arrays.copyOfRange(packet.getData(), packet.getOffset(),packet.getOffset()+packet.getLength());
			
			System.out.println("Server Recive data:"+new String(recvMsg));
			
		}
	}

}
