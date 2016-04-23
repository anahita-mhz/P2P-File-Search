package ProgramLogic;

import java.util.concurrent.BlockingQueue;

import Network.Network_Control_Message;

public class Delayer_Child_Thread extends Thread
{
	BlockingQueue<Network_Control_Message> delayed_queue;
	Network_Control_Message pckt;
	int delay_time;
	
	public Delayer_Child_Thread(
			BlockingQueue<Network_Control_Message> delayed_queue,
			Network_Control_Message pckt,
			int delay_time)
	{
		super();
		this.delayed_queue = delayed_queue;
		this.pckt = pckt;
		this.delay_time = delay_time;
	}
	
	public void run()
	{
		// Wait for the delay_time: Mimic the propagation delay
		try {
			Thread.sleep(delay_time);
		} catch (InterruptedException e) {
			System.out.println("Error, could not sleep. So, could not delay the packet.");
			System.out.println(e.getMessage());
		}
		// Make a artificial delivary. Add the packet to the delayed_queue.
		try {
			delayed_queue.put(pckt);
		} catch (InterruptedException e) {
			System.out.println("Error, could not add the delayed packet.");
			System.out.println(e.getMessage());
		}
	}
}
