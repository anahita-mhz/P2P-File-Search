package Network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;

public class Network_Main
{
	private static ServerSocket server_socket;
	private static BlockingQueue<Network_Control_Message> send_queue;
	private static BlockingQueue<Network_Control_Message> receive_queue;
	private static Vector<Socket> sockets;
	private static int port;
	
	public static void set_variables(BlockingQueue<Network_Control_Message> send_queue,
			BlockingQueue<Network_Control_Message> receive_queue, int port)
	{
		Network_Main.send_queue = send_queue;
		Network_Main.receive_queue = receive_queue;
		Network_Main.port = port;
		Network_Main.sockets = new Vector<Socket>();
	}

	public static void start(
			BlockingQueue<Network_Control_Message> send_queue,
			BlockingQueue<Network_Control_Message> receive_queue, int port)
	{
		set_variables(send_queue, receive_queue, port);
		try {
			server_socket = new ServerSocket(Network_Main.port);
			
			// Make a listener
			Network_Listener_Thread listener = new Network_Listener_Thread(Network_Main.receive_queue, server_socket, sockets);
			listener.start();
			
			Network_Sender_Thread sender = new Network_Sender_Thread(send_queue, sockets);
			sender.start();
			
		} catch (IOException e) {
			System.out.println("Cannot listen on port " + port);
			System.out.println(e.getMessage());
		}
	}
	
	public static void terminate_all_connections()
	{
		// Sending a message with terminate flag on so that sender close all the connections.
		Network_Control_Message pckt = new Network_Control_Message();
		pckt.set_terminate(true);
		send_queue.add(pckt);
		
		try {
			server_socket.close();
		} catch (IOException e) {
			System.out.println("Could not close the ServerSocket.");
			System.out.println(e.getMessage());
		}
	}
}
