package ProgramLogic;

import java.io.Serializable;

public class Multicast_Message implements Serializable
{
	private static final long serialVersionUID = 8127552876371439146L;
	
	public long msg_physical_timestamp; // Physical Timestamp of the message
	public int[][] msg_matrix;			// The matrix of the message
	public String type;					// The type specifies if the message is ordinary or a completion notification
	public int sender_id;				// The sender of the message
	
	public Multicast_Message(int[][] msg_matrix, String type, int sender_id)
	{
		super();
		this.msg_matrix = msg_matrix;
		this.msg_physical_timestamp = System.currentTimeMillis();
		this.type = type;
		this.sender_id = sender_id;
	}
}
