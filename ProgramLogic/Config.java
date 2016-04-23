package ProgramLogic;

import java.util.concurrent.Semaphore;

public class Config
{
	public int NODE_ID;
	public int NUM_OF_NODES;
	public String[] NODES_ADDRESSES;
	public int[] NODES_PORTS;
	public boolean IS_BROADCAST;	// Check if the program is ran as broadcast or multicast
	
	private Semaphore synch;		// synch semaphore is used so that the Node 0 can informed once all
									// 		the completion notifications from other nodes received.
	public Reporter reporter;
	
	public Config(Semaphore synch)
	{
		super();
		this.synch = synch;
	}
	
	public void wait_synch()
	{
		try {
			synch.acquire();
		} catch (InterruptedException e) {
			System.out.println("Error, could not wait on semaphore.");
			System.out.println(e.getMessage());
		}
	}
	
	public void signal_synch()
	{
		synch.release();
	}
}
