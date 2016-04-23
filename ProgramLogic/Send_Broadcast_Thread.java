package ProgramLogic;

import java.util.concurrent.BlockingQueue;

import Network.*;

public class Send_Broadcast_Thread extends Thread
{
	Config config;
	BlockingQueue<Network_Control_Message> send_queue;
	Broadcast_Causal_Info causal_info;
	Data_Collector data_collector;

	public Send_Broadcast_Thread(Config config,
			BlockingQueue<Network_Control_Message> send_queue,
			Broadcast_Causal_Info causal_info,
			Data_Collector data_collector)
	{
		super();
		this.config = config;
		this.send_queue = send_queue;
		this.causal_info = causal_info;
		this.data_collector = data_collector;
	}

	public void run()
	{
		// Sending 100 Messages
		for(int i = 0 ; i < 100 ; i++)
		{
			wait_lb_ub(20, 100);				// Wait
			causal_info.increase(); 			// Increase the vector
			Broadcast_Message msg = new Broadcast_Message
					(copy_vector(causal_info.getVector()), "oridnary", config.NODE_ID);
			broadcast(msg);						// Broadcasting a new Message
			data_collector.increase_sent_messages();
		}
		
		// After sending 100 messages send the completion notification to node zero (including itself as it is written)
		Broadcast_Message com_not = new Broadcast_Message
				(causal_info.getVector(), "completion_notification", config.NODE_ID);
		unicast(com_not, 0);
		
		if(config.NODE_ID == 0)
		{
			// Wait to receive all the competion notifications
			config.wait_synch();
			Broadcast_Message terminate = new Broadcast_Message
					(causal_info.getVector(), "terminate", config.NODE_ID);
			// Send Terminate Message to all nodes that they can be shutdown.
			for (int i = 0 ; i < config.NUM_OF_NODES ; i++)
				unicast(terminate, i);
		}
	}

	// Just copying a vector
	private int[] copy_vector(int[] vector)
	{
		int[] new_vector = new int[vector.length];
		for(int i = 0 ; i < vector.length ; i++)
			new_vector[i] = vector[i];
		return new_vector;
	}

	// broadcasting a message
	private void broadcast(Broadcast_Message msg)
	{
		for(int i = 0 ; i < config.NUM_OF_NODES ; i++)
		{
			if(i == config.NODE_ID)
				continue;
			unicast(msg, i);
		}
	}
	
	// Sending a message to a node
	private void unicast(Broadcast_Message msg, int receiver_node_id)
	{
		String receiver_address = config.NODES_ADDRESSES[receiver_node_id];
		int receiver_port = config.NODES_PORTS[receiver_node_id];
		
		try {
			send_queue.put(new Network_Control_Message(msg, receiver_address, receiver_port));
		} catch (InterruptedException e) {
			System.out.println("Cannot put a message in send_queue.");
			System.out.println(e.getMessage());
		}
	}
	
	// Waiting
	private void wait_lb_ub(int lb, int ub)
	{
		int sleep_time = (int) (Math.random() * (ub-lb+1) + lb);
		try {
			sleep(sleep_time);
		} catch (InterruptedException e) {
			System.out.println("Error, thread cannot sleep.");
			System.out.println(e.getMessage());
		}
	}
}
