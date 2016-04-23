package ProgramLogic;

import java.util.concurrent.BlockingQueue;

import Network.Network_Control_Message;

public class Delayer_Thread extends Thread
{
	BlockingQueue<Network_Control_Message> receive_queue; // The queue through which the Network module sends the received packets
	BlockingQueue<Network_Control_Message> delayed_queue; // The queue from which the sender reads the packets delayed artificially
	
	public Delayer_Thread(
			BlockingQueue<Network_Control_Message> receive_queue,
			BlockingQueue<Network_Control_Message> delayed_queue) {
		super();
		this.receive_queue = receive_queue;
		this.delayed_queue = delayed_queue;
	}
	
	public void run()
	{		
		while(true)
		{
			try {
				Network_Control_Message pckt = receive_queue.take();
				int delay_time = (int)(Math.random()*151) + 50;
				// Making a delay through a thread that sleeps for the delay time and
				//		then add the message to the delayed queue
				(new Delayer_Child_Thread(delayed_queue, pckt, delay_time)).start();
				// If the packet was the terminate then return to terminate the program.
				if(pckt.get_msg() instanceof Broadcast_Message)
				{
					if(((Broadcast_Message)pckt.get_msg()).type.compareTo("terminate") == 0)
						return;
				}
				if(pckt.get_msg() instanceof Multicast_Message)
				{
					if(((Multicast_Message)pckt.get_msg()).type.compareTo("terminate") == 0)
						return;
				}
			} catch (InterruptedException e) {
				System.out.println("Error, cannot read from receive_queue.");
				System.out.println(e.getMessage());
			}
		}
	}
}
