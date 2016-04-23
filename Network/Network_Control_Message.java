package Network;

import java.io.Serializable;

public class Network_Control_Message
{
	private Serializable msg;
	private String sender_address;
	private int sender_port;
	private String receiver_address;
	private int receiver_port;
	private boolean is_terminate;
	
	public Network_Control_Message(Serializable msg, String receiver_address, int receiver_port)
	{
		super();
		this.msg = msg;
		this.receiver_address = receiver_address;
		this.receiver_port = receiver_port;
		this.is_terminate = false;
	}
	
	public Network_Control_Message(Serializable msg, String sender_address, int sender_port, String receiver_address, int receiver_port)
	{
		super();
		this.msg = msg;
		this.sender_address = sender_address;
		this.sender_port = sender_port;
		this.receiver_address = receiver_address;
		this.receiver_port = receiver_port;
		this.is_terminate = false;
	}

	public Network_Control_Message()
	{
		super();
		this.is_terminate = false;
	}
	
	public Serializable get_msg()
	{
		return msg;
	}

	public void set_msg(Serializable msg)
	{
		this.msg = msg;
	}

	public String get_sender_address()
	{
		return sender_address;
	}

	public void set_sender_address(String sender_address)
	{
		this.sender_address = sender_address;
	}

	public int get_sender_port()
	{
		return sender_port;
	}

	public void set_sender_port(int sender_port)
	{
		this.sender_port = sender_port;
	}

	public String get_receiver_address()
	{
		return receiver_address;
	}

	public void set_receiver_address(String receiver_address)
	{
		this.receiver_address = receiver_address;
	}

	public int get_receiver_port()
	{
		return receiver_port;
	}

	public void set_receiver_port(int receiver_port)
	{
		this.receiver_port = receiver_port;
	}
	
	boolean is_terminate()
	{
		return is_terminate;
	}
	
	void set_terminate(boolean should_terminate)
	{
		this.is_terminate = should_terminate;
	}
}
