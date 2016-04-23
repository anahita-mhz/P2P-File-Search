package ProgramLogic;

import java.util.concurrent.BlockingQueue;

import Network.Network_Control_Message;

public class Send_Multicast_Thread extends Thread
{
	Config config;
	BlockingQueue<Network_Control_Message> send_queue;
	Multicast_Causal_Info causal_info;
	Data_Collector data_collector;

	public Send_Multicast_Thread(Config config,
			BlockingQueue<Network_Control_Message> send_queue,
			Multicast_Causal_Info causal_info,
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
		for(int i = 0 ; i < 100 ; i++)
		{
			wait_lb_ub(20, 100);					// Wait
			int[] chosens = choose_nodes();			// Choose to which nodes send a message
			causal_info.increase(chosens);
			Multicast_Message msg = new Multicast_Message
					(copy_matrix(causal_info.get_matrix()), "oridnary", config.NODE_ID);
			multicast(msg, chosens);				// Multicast to nodes
			data_collector.increase_sent_messages();
		}
		
		// Send completion notification to node 0 (including itself)
		Multicast_Message com_not = new Multicast_Message
				(causal_info.get_matrix(), "completion_notification", config.NODE_ID);
		unicast(com_not, 0);
		
		if(config.NODE_ID == 0)
		{
			// Wait for receiving all the completion notification messages
			config.wait_synch();
			Multicast_Message terminate = new Multicast_Message
					(causal_info.get_matrix(), "terminate", config.NODE_ID);
			for (int i = 0 ; i < config.NUM_OF_NODES ; i++)
				unicast(terminate, i);
		}
	}
	
	// Coping matrix
	private int[][] copy_matrix(int[][] matrix)
	{
		int[][] new_matrix = new int[matrix.length][matrix[0].length];
		for(int i = 0 ; i < matrix.length ; i++)
			for(int j = 0 ; j < matrix.length ; j++)
				new_matrix[i][j] = matrix[i][j];
		return new_matrix;
	}

	// Choose to which nodes send the message
	private int[] choose_nodes()
	{
		int rand_cnt = (int)(Math.random()*(config.NUM_OF_NODES - 1)) + 1;
		int[] chosens = new int[rand_cnt];
		
		int[] all_num = new int[config.NUM_OF_NODES];
		for(int j = 0 ; j < config.NUM_OF_NODES ; j++)
			all_num[j] = j;
		all_num[config.NODE_ID] = 0;
		all_num[0] = config.NODE_ID;
		for(int j = 0 ; j < rand_cnt ; j++)
		{
			int rand = (int)(Math.random()*(config.NUM_OF_NODES - 1 - j)) + 1 + j;
			chosens[j] = all_num[rand];
			all_num[rand] = all_num[j+1];
		}
		
		return chosens;
	}

	// Multicast sending
	private void multicast(Multicast_Message msg, int[] chosens)
	{
		for(int i = 0 ; i < chosens.length ; i++)
			unicast(msg, chosens[i]);
	}
	
	// Sending a message to one node
	private void unicast(Multicast_Message msg, int receiver_node_id)
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
