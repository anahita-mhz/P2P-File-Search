package ProgramLogic;

public class Broadcast_Causal_Info
{
	private int NODE_ID;
	private int NUM_OF_NODES;
	
	// The vector for the algorithm
	private int[] vector;
	
	public Broadcast_Causal_Info(int NODE_ID, int NUM_OF_NODES)
	{
		super();
		this.NODE_ID = NODE_ID;
		this.NUM_OF_NODES = NUM_OF_NODES;
		this.vector = new int[NUM_OF_NODES];
	}

	synchronized public void increase()
	{
		this.vector[this.NODE_ID] = this.vector[this.NODE_ID] + 1;
	}
	
	// Checks if the msg should be buffered.
	synchronized public boolean should_buffer(Broadcast_Message msg)
	{
		int sender = msg.sender_id;
		if(sender == this.NODE_ID && msg.msg_vector[sender] != this.vector[sender])
			System.out.println("!!!???WHAT THE PHASE???!!!");
		for(int i = 0 ; i < this.NUM_OF_NODES ; i++)
			if(msg.msg_vector[i] > this.vector[i] && i != sender)
				return true;
		if(msg.msg_vector[sender] == this.vector[sender] + 1)
			return false;
		if(sender == this.NODE_ID && msg.msg_vector[sender] == this.vector[sender])
		{
			System.out.println("!!!???WHAT THE PHASE???!!!");
			return false;
		}
		return true;
	}
	
	// Updating the vector by another vector 
	synchronized public void update(int[] vector)
	{
		int[] new_vector = new int[vector.length];
		for(int i = 0 ; i < this.NUM_OF_NODES ; i++)
			new_vector[i] = Math.max(this.vector[i], vector[i]);
		String screen_msg = String.format("Vector is updated from %s\n\t\t    to %s.",
				int_array_to_string(this.vector), int_array_to_string(new_vector));
		this.vector = new_vector;
		System.out.println(screen_msg);
	}
	
	// Printing the vector
	synchronized private String int_array_to_string(int[] array)
	{
		String str = "";
		for (int i = 0; i < array.length - 1 ; i++)
			str += String.format("%3d ", array[i]);
		str += String.format("%3d", array[array.length - 1]);
		return str;
	}

	public int[] getVector()
	{
		return vector;
	}
}