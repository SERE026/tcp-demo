package com.sere.socket.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class UnicastServer {

	public static int MAX_RECEVED = 255;
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		DatagramSocket ds = new DatagramSocket(18095);
		
		DatagramPacket packet = new DatagramPacket(new byte[MAX_RECEVED], MAX_RECEVED);
		
		while(true){
			ds.receive(packet);
			
			byte[] recvMsg = Arrays.copyOfRange(packet.getData(), packet.getOffset(),packet.getOffset()+packet.getLength());
			
			System.out.println("Server Recive data:"+new String(recvMsg));
			
			ds.send(packet);
		}
	}

}
