package ProgramLogic;

import Network.Network_Control_Message;

// This is the SPECIAL Message wrapes the buffered messages with a timestamp showing the time by which they buffered.  
public class Network_Control_Message_Buff
{
	Network_Control_Message pckt;
	long buff_timestamp;
	
	public Network_Control_Message_Buff(
			Network_Control_Message pckt,
			long buff_timestamp)
	{
		super();
		this.pckt = pckt;
		this.buff_timestamp = buff_timestamp;
	}
}
