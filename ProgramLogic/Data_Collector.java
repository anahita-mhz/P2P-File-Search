package ProgramLogic;

import java.util.Vector;

public class Data_Collector
{
	public Data_Collector()
	{
		super();
		latencies = new Vector<Integer>();
	}

	// The information required by the project
	private Vector<Integer> latencies;
	private int number_of_messages_received;
	private int number_of_messages_sent;
	private int total_latency;
	private double latency_deviation;
	private int number_of_messages_buffered;
	private int number_of_messages_not_buffered;
	private int total_time_buffered;
	private int max_time_buffered;
	private long time_averaged_buffered;
	private int buffered_peak;
	
	void add_time_average_buffered(long amount)
	{
		this.time_averaged_buffered += amount;
	}
	
	void check_if_buffered_peak(int number)
	{
		this.buffered_peak = (this.buffered_peak < number ? number : this.buffered_peak);
	}
	
	void increase_received_messages()
	{
		this.number_of_messages_received++;
	}
	
	void increase_sent_messages()
	{
		this.number_of_messages_sent++;
	}
	
	void add_latency(int amount)
	{
		latencies.addElement(amount);
		this.total_latency += amount;
	}
	
	void calculate_latency_deviation()
	{
		double average = (double)(total_latency)/latencies.size();
		double sigma = 0;
		for(int i = 0 ; i < latencies.size() ; i++)
			sigma += Math.pow((latencies.get(i)-average), 2);
		sigma /= latencies.size();
		sigma = Math.sqrt(sigma);
		latency_deviation = sigma;
	}
	
	void increase_buffered_counter()
	{
		this.number_of_messages_buffered++;
	}
	
	void add_time_buffered(int amount)
	{
		this.total_time_buffered += amount;
		check_if_max_time_buffered(amount);
	}
	
	private void check_if_max_time_buffered(int amount)
	{
		max_time_buffered = (max_time_buffered < amount ? amount : max_time_buffered);
	}
	
	void increase_not_buffered_counter()
	{
		this.number_of_messages_not_buffered++;
	}

	public int get_number_of_messages_received()
	{
		return number_of_messages_received;
	}

	public int get_number_of_messages_sent()
	{
		return number_of_messages_sent;
	}
	
	public int get_total_latency()
	{
		return total_latency;
	}

	public int get_number_of_messages_buffered()
	{
		return number_of_messages_buffered;
	}

	public int get_number_of_messages_not_buffered()
	{
		return number_of_messages_not_buffered;
	}

	public int get_total_time_buffered()
	{
		return total_time_buffered;
	}

	public int get_max_time_buffered()
	{
		return max_time_buffered;
	}

	public double get_latency_deviation()
	{
		return latency_deviation;
	}

	public long get_time_averaged_buffered() {
		return time_averaged_buffered;
	}

	public int get_buffered_peak() {
		return buffered_peak;
	}
}
