package ProgramLogic;

public class Multicast_Causal_Info
{
	private int NODE_ID;
	private int NUM_OF_NODES;
	
	private int[][] matrix; // The matrix of the causal order algorithm for multicasting
	
	public Multicast_Causal_Info(int NODE_ID, int NUM_OF_NODES)
	{
		super();
		this.NODE_ID = NODE_ID;
		this.NUM_OF_NODES = NUM_OF_NODES;
		this.matrix = new int[NUM_OF_NODES][NUM_OF_NODES];
	}

	synchronized public void increase(int[] nodes)
	{
		for(int i = 0 ; i < nodes.length ; i++)
			this.matrix[this.NODE_ID][nodes[i]]++;
	}
	
	// Check if a message should be buffered or not
	synchronized public boolean should_buffer(Multicast_Message msg)
	{
		int sender = msg.sender_id;
		if(sender == this.NODE_ID && msg.msg_matrix[this.NODE_ID][sender] == this.matrix[this.NODE_ID][sender])
			System.out.println("!!!???WHAT THE PHASE???!!!");
		for(int j = 0 ; j < this.NUM_OF_NODES ; j++)
			if(msg.msg_matrix[j][this.NODE_ID] > this.matrix[j][this.NODE_ID] && j != sender)
				return true;
		if(msg.msg_matrix[sender][this.NODE_ID] == this.matrix[sender][this.NODE_ID] + 1)
			return false;
		if(sender == this.NODE_ID && msg.msg_matrix[this.NODE_ID][sender] == this.matrix[this.NODE_ID][sender])
		{
			System.out.println("!!!???WHAT THE PHASE???!!!");
			return false;
		}
		return true;
	}
	
	// Updating the matrix of the algorithm
	synchronized public void update(int[][] matrix)
	{
		int[][] new_matrix = new int[this.NUM_OF_NODES][NUM_OF_NODES];
		for(int i = 0 ; i < this.NUM_OF_NODES ; i++)
			for(int j = 0 ; j < this.NUM_OF_NODES ; j++)
				new_matrix[i][j] = Math.max(this.matrix[i][j], matrix[i][j]);
		String screen_msg = String.format("Matrix is updated from\n%sto\n%s\n",
				int_matrix_to_string(this.matrix), int_matrix_to_string(new_matrix));
		this.matrix = new_matrix;
		System.out.println(screen_msg);
	}
	
	// Printing the matrix
	synchronized private String int_matrix_to_string(int[][] matrix)
	{
		String str = "";
		for (int i = 0; i < matrix.length ; i++)
		{
			str += "\t\t";
			for (int j = 0; j < matrix[i].length - 1 ; j++)
				str += String.format("%3d ", matrix[i][j]);
			str += String.format("%3d\n", matrix[i][matrix[i].length - 1]);
		}
		return str;
	}

	public int[][] get_matrix()
	{
		return matrix;
	}
}