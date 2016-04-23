package Network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

class Network_Receiver_Thread extends Thread
{
	private BlockingQueue<Network_Control_Message> receive_queue;
	private ObjectInputStream in;
	private Socket socket;
	
	public Network_Receiver_Thread(Socket socket, BlockingQueue<Network_Control_Message> receive_queue)
	{
		super();
		this.socket = socket;
		this.receive_queue = receive_queue;
	}
	
	public void run()
	{
		try {
			in = new ObjectInputStream(socket.getInputStream());
			// While true receive a message from the network and put it in the queue
			while(true)
			{
				try {
					Serializable msg = (Serializable) in.readObject();
					
					Network_Control_Message pckt = new Network_Control_Message(
							msg,
							socket.getInetAddress().getHostAddress(),
							socket.getPort(),
							socket.getLocalAddress().getHostAddress(),
							socket.getLocalPort());
					receive_queue.put(pckt);
				} catch (InterruptedException e) {
					System.out.println("Cannot put a message in receive_queue.");
					System.out.println(e.getMessage());
				}
			}
		} catch (IOException e) {
			return;
		} catch (ClassNotFoundException e) {
			System.out.println("Cannot cast the object read to Packet.");
			System.out.println(e.getMessage());
		}
	}
}
