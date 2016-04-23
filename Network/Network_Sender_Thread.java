package Network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;

class Network_Sender_Thread extends Thread
{
	private BlockingQueue<Network_Control_Message> send_queue;
	private Vector<Socket> sockets;
	private Vector<ObjectOutputStream> outs;
	
	public Network_Sender_Thread(BlockingQueue<Network_Control_Message> send_queue, Vector<Socket> sockets)
	{
		super();
		this.send_queue = send_queue;
		this.sockets = sockets;
	}
	
	public void run()
	{
		outs = new Vector<ObjectOutputStream>();
		while(true)
		{
			// While True check if a message should be sent.
			try {
				Network_Control_Message pckt = send_queue.take();
				// Terminate message will not be sent to nodes. It is just for closing connections. Looks like a flag.
				if(pckt.is_terminate())
				{
					close_all_connections();
					return;
				}
				int sender_index = get_connection(pckt.get_receiver_address(), pckt.get_receiver_port());
				ObjectOutputStream out = outs.get(sender_index);
				out.writeObject(pckt.get_msg());
				out.reset();						// reset is not needed, but to be on the sage side I used that.
			} catch (InterruptedException e) {
				System.out.println("Error, cannot read from BlockingQueue.");
				System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println("Error, cannot write an Object in the ObjectOutputStream.");
				System.out.println(e.getMessage());
			}
		}
	}

	private void close_all_connections()
	{
		for (Socket socket : sockets)
		{
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("Cannot close the socket.");
				System.out.println(e.getMessage());
			}
		}
	}

	private int get_connection(String receiver_address, int receiver_port)
	{
		for(int i = 0 ; i < sockets.size() ; i++)
		{
			if(receiver_address.compareTo(sockets.get(i).getInetAddress().getHostAddress()) == 0
					&& receiver_port == sockets.get(i).getPort())
				return i;
		}
		return make_new_connection(receiver_address, receiver_port);
	}

	private int make_new_connection(String receiver_address, int receiver_port)
	{
		while(true)
		{
			try {
				Socket socket = new Socket(receiver_address, receiver_port);
				sockets.add(socket);
				outs.add(new ObjectOutputStream(socket.getOutputStream()));
				return outs.size()-1;
			} catch (Exception e) { }
		}
	}
}
