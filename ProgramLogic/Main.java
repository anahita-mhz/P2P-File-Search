package ProgramLogic;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import Network.*;

//MAIN Class for starting the Program
public class Main
{
	public static void main(String[] args)
	{
		// Shared information such as node id and etc.
		Config config = new Config(new Semaphore(0));
		
		// Return if the config provided is not of the correct format.
		if(!read_config(config, args))
			return;
		
		// A queue by which the network module sends the messages (The unit is Network_Control_Message Class).
		BlockingQueue<Network_Control_Message> receive_queue = 
				new LinkedBlockingQueue<Network_Control_Message>();
		// The Delayer_Thread delays the messages sent from the network and
		// 		the Receiver_Thread(s) should read from this delay queue.
		BlockingQueue<Network_Control_Message> delayed_queue = 
				new LinkedBlockingQueue<Network_Control_Message>();
		// Send queue so that the Send_Thread(s) write inside it and the Network module send them to other Processes
		BlockingQueue<Network_Control_Message> send_queue = 
				new LinkedBlockingQueue<Network_Control_Message>();
		
		// Start the Network by sending it the queues it expects and the port from which should listen
		Network_Main.start(send_queue, receive_queue, config.NODES_PORTS[config.NODE_ID]);
		
		// The Delayer that get the received messages by the network and
		// 		put them in delayed queue after a random delay time
		(new Delayer_Thread(receive_queue, delayed_queue)).start();
		
		// The reporter that report the data collected
		Reporter reporter = new Reporter
				("Report_" + config.NODE_ID + ".TXT", "Screen_" + config.NODE_ID + ".TXT");
		
		// Data_Collecter is the object that saves the information during the execution
		Data_Collector data_collector = new Data_Collector();
		
		// Depends on the setting provided decide to do as broadcast or multicast 
		if(config.IS_BROADCAST)
			call_broadcast_modules(config, send_queue, delayed_queue, data_collector, reporter);
		else
			call_multicast_modules(config, send_queue, delayed_queue, data_collector, reporter);
	}

	// Making threads appropriate for multicast
	private static void call_multicast_modules(Config config,
			BlockingQueue<Network_Control_Message> send_queue,
			BlockingQueue<Network_Control_Message> delayed_queue,
			Data_Collector data_collector,
			Reporter reporter)
	{
		// causal_info is the vector/matrix
		Multicast_Causal_Info causal_info = 
				new Multicast_Causal_Info(config.NODE_ID, config.NUM_OF_NODES);
		
		// Make a multicast sender to send messages (100 times)
		Send_Multicast_Thread sender = new Send_Multicast_Thread
				(config, send_queue, causal_info, data_collector);
		sender.start();
		
		// Make a multicast receiver to handle the messages received
		Receive_Multicast_Thread receiver = new Receive_Multicast_Thread
				(config, delayed_queue, causal_info, data_collector, reporter);
		
		receiver.start();
	}

	// Making threads appropriate for broadcast
	private static void call_broadcast_modules(Config config,
			BlockingQueue<Network_Control_Message> send_queue,
			BlockingQueue<Network_Control_Message> delayed_queue,
			Data_Collector data_collector,
			Reporter reporter)
	{
		// causal_info is the vector/matrix
		Broadcast_Causal_Info causal_info = 
				new Broadcast_Causal_Info(config.NODE_ID, config.NUM_OF_NODES);
		
		// Make a broadcast sender to send messages (100 times)
		Send_Broadcast_Thread sender = new Send_Broadcast_Thread
				(config, send_queue, causal_info, data_collector);
		sender.start();
		
		// Make a broadcast receiver to handle the messages received
		Receive_Broadcast_Thread receiver = new Receive_Broadcast_Thread
				(config, delayed_queue, causal_info, data_collector, reporter);
		receiver.start();
	}

	// Reads the config from the config file and the console
	private static boolean read_config(Config config, String[] args) {
		try{
			// Reading the node_id from the console
			config.NODE_ID = Integer.parseInt(args[0]);
			if(args.length > 1 && args[1].compareTo("--broadcast") == 0)
				config.IS_BROADCAST = true;
			
			// Reading other information from config.txt
			Scanner sc = new Scanner(new File("config.txt"));
			while(sc.hasNextLine())
			{
				String line = sc.nextLine();
				if(line.compareTo("# NumberOfNodes") == 0)
					config.NUM_OF_NODES = sc.nextInt();
				if(line.compareTo("# NodesAddresses") == 0)
				{
					config.NODES_ADDRESSES = new String[config.NUM_OF_NODES];
					for(int i = 0 ; i < config.NUM_OF_NODES ; i++)
						config.NODES_ADDRESSES[i] = sc.nextLine();
				}
				if(line.compareTo("# NodesPorts") == 0)
				{
					config.NODES_PORTS = new int[config.NUM_OF_NODES];
					for(int i = 0 ; i < config.NUM_OF_NODES ; i++)
						config.NODES_PORTS[i] = sc.nextInt();
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
			System.out.println("The Configuration file named \"config.txt\" does not exists.");
			return false;
		} catch (Exception e) {
			// Showing help as the information provided was not in the format expected.
			if(args.length < 1 || args[0].compareTo("--help") != 0)
				System.out.println("Error, wrong usage or wrong config file format.");
			show_help();
			return false;
		}
		return true;
	}

	// Help of the program
	private static void show_help()
	{
		System.out.println("\n***************************************************************************************");
		System.out.println("\nCS 6378 Projetc I: ");
		System.out.println("\tImplementing Causaly Ordered Algorithms for Multicast and Broadcast.");
		System.out.println("Correct usage: java Main NODE_ID [options]");
		System.out.println("Options: ");
		System.out.println("\t--broadcast\tSetting the program to work such that all\n"
				+ "\t           \tnodes are expected to only send broadcast messages.\n");
		System.out.println("\t--help     \tShowing the help.\n");
		System.out.println("***************************************************************************************");
	}
}