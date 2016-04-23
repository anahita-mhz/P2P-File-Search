package Network;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;

class Network_Listener_Thread extends Thread
{
	ServerSocket server_socket;
	BlockingQueue<Network_Control_Message> receive_queue;
	Vector<Socket> sockets;
	
	public Network_Listener_Thread (BlockingQueue<Network_Control_Message> receive_queue,
			ServerSocket server_socket, Vector<Socket> sockets)
	{
		super();
		this.receive_queue = receive_queue;
		this.server_socket = server_socket;
		this.sockets = sockets;
	}
	
	public void run()
	{
		try {
			while(true)
			{
				// Listen on the specified port and accept the connection and make a thread to handle the information sent.
				Socket socket = server_socket.accept();
				Network_Receiver_Thread receiver = new Network_Receiver_Thread(socket, receive_queue);
				receiver.start();
			}
		} catch (Exception e) {
			System.out.println("Server Socket closed.");
		}
	}
}
