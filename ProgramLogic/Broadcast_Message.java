package ProgramLogic;

import java.io.Serializable;

public class Broadcast_Message implements Serializable
{
	private static final long serialVersionUID = 7880485755954377535L;
	
	public long msg_physical_timestamp; // The Physical Time Stamp
	public int[] msg_vector;			// The vector included in the message
	public String type;					// Specifies if it is a notification completion message or an ordinary one
	public int sender_id;				// The sender of the message
	
	public Broadcast_Message(int[] msg_vector, String type, int sender_id)
	{
		super();
		this.msg_vector = msg_vector;
		this.msg_physical_timestamp = System.currentTimeMillis();
		this.type = type;
		this.sender_id = sender_id;
	}
}
