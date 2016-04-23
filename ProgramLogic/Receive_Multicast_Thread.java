package ProgramLogic;

import java.util.Vector;
import java.util.concurrent.BlockingQueue;

import Network.Network_Control_Message;
import Network.Network_Main;

public class Receive_Multicast_Thread extends Thread
{
	Config config;
	BlockingQueue<Network_Control_Message> delayed_queue;
	Vector<Network_Control_Message_Buff> buffer = new Vector<Network_Control_Message_Buff>();
	boolean[] com_notifs;
	Multicast_Causal_Info causal_info;
	Data_Collector data_collector;
	Reporter reporter;
	long last_buffer_updated_time;

	public Receive_Multicast_Thread(Config config,
			BlockingQueue<Network_Control_Message> delayed_queue,
			Multicast_Causal_Info causal_info,
			Data_Collector data_collector,
			Reporter reporter)
	{
		super();
		this.config = config;
		this.delayed_queue = delayed_queue;
		this.causal_info = causal_info;
		this.data_collector = data_collector;
		this.reporter = reporter;
	}

	public void run()
	{
		com_notifs = new boolean[config.NUM_OF_NODES];
		while(true)
		{
			try {
				Network_Control_Message pckt = delayed_queue.take();
				Multicast_Message msg = (Multicast_Message)pckt.get_msg();
				long curr_time = System.currentTimeMillis();
				// check if it is a completion notification
				if(msg.type.compareTo("completion_notification") == 0)
				{
					// It should be from node 0.
					if(config.NODE_ID == 0)
					{
						int sender_id = msg.sender_id;
						com_notifs[sender_id] = true;
						boolean is_all_complete = true;
						// check if all of the nodes sent this message
						for(int i = 0 ; i < config.NUM_OF_NODES ; i++)
							if(!com_notifs[i])
							{
								is_all_complete = false;
								break;
							}
						// If all the nodes sent
						if(is_all_complete)
						{
							// Signal if nodes 0 waits for all the completion messages received.
							config.signal_synch();
							System.out.println("Receiving all completion notification messages.\r\n"
									+ "Bringing the entire distributed computation to an end.");
						}
					}
					else
						System.out.println("!!!???WHAT THE PHASE???!!!");
				}
				// if it is a terminate message
				else if(msg.type.compareTo("terminate") == 0)
				{
					// If we should terminate, calculate the latency deviation and write the reports.
					data_collector.calculate_latency_deviation();
					reporter.report(data_collector);
					reporter.screen_pw_close();
					System.out.printf("Terminating all connections. Node number: %d.\r\n", config.NODE_ID);
					Network_Main.terminate_all_connections();
					return;
				}
				else
				{
					// If an ordinary message, so add to the data collector the information.
					data_collector.increase_received_messages();
					if(causal_info.should_buffer(msg))
					{
						data_collector.add_time_average_buffered(buffer.size()*(curr_time - last_buffer_updated_time));
						last_buffer_updated_time = curr_time;
						data_collector.increase_buffered_counter();
						buff(new Network_Control_Message_Buff(pckt, System.currentTimeMillis()));
						data_collector.check_if_buffered_peak(buffer.size());
					}
					else
					{
						int latency = (int) (curr_time - msg.msg_physical_timestamp);
						data_collector.add_latency(latency);
						data_collector.increase_not_buffered_counter();
						deliver(pckt);
						// After delivering a message maybe we can remove a message from buffer.
						check_buffereds();
					}
				}
			} catch (InterruptedException e) {
				System.out.println("Error, receiver cannot read from blocking queue.");
			}
		}
	}

	// Check if any message could be removed from buffer
	private void check_buffereds()
	{
		long curr_time = System.currentTimeMillis();
		int counter = buffer.size();
		boolean is_buffer_changed = false;
		while(counter > 0)
		{
			Network_Control_Message_Buff buff_pckt = buffer.remove(0);
			Network_Control_Message pckt = buff_pckt.pckt;
			Multicast_Message msg = (Multicast_Message)pckt.get_msg();
			if(causal_info.should_buffer(msg))
				buffer.addElement(buff_pckt);
			else
			{
				is_buffer_changed = true;
				data_collector.add_time_average_buffered(buffer.size()*(curr_time - last_buffer_updated_time));
				// The time the message was buffered is calculated.
				int latency = (int)(curr_time - buff_pckt.buff_timestamp);
				data_collector.add_time_buffered(latency);
				String out = String.format("Buffered message with timestamp %s from node %d is delivered.\r\n",
						msg.msg_physical_timestamp,
						msg.sender_id);
				System.out.print(out);
				reporter.screen(out);
				deliver(pckt);
			}
			counter--;
		}
		if(is_buffer_changed)
			last_buffer_updated_time = curr_time;
	}

	// Simply adding the message to the buffer.
	private void buff(Network_Control_Message_Buff pckt)
	{
		Multicast_Message msg = (Multicast_Message)pckt.pckt.get_msg();
		buffer.addElement(pckt);
		int sender_node_id = msg.sender_id;
		String out = "Message from node " + sender_node_id + " with physical timestamp "
				+ msg.msg_physical_timestamp + " is buffered.\r\n";
		out += String.format("Message matrix is\r\n%s\r\n", int_matrix_to_string(msg.msg_matrix));
		out += String.format("Process matrix is\r\n%s\r\n", int_matrix_to_string(causal_info.get_matrix()));
		System.out.print(out);
		reporter.screen(out);
	}
	
	// Simply deliver the message by printing.
	private void deliver(Network_Control_Message pckt)
	{
		Multicast_Message msg = (Multicast_Message)pckt.get_msg();
		causal_info.update(msg.msg_matrix);
		int sender_node_id = msg.sender_id;
		String out = "Message from node " + sender_node_id + " with physical timestamp "
				+ msg.msg_physical_timestamp + " is delivered.\r\n";
		System.out.print(out);
		reporter.screen(out);
	}
	
	// Print the vector to the screen 
	private String int_matrix_to_string(int[][] matrix)
	{
		String str = "";
		for (int i = 0; i < matrix.length ; i++)
		{
			str += "\t\t";
			for (int j = 0; j < matrix[i].length - 1 ; j++)
				str += String.format("%3d ", matrix[i][j]);
			str += String.format("%3d\r\n", matrix[i][matrix[i].length - 1]);
		}
		return str;
	}
}
