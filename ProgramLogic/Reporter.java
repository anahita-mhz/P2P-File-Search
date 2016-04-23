package ProgramLogic;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Reporter
{	
	private PrintWriter report_wr; // Printwriter to report the data collected to the Report files.
	private PrintWriter screen_wr; // Printwriter to write all the information on the screen to the Screen files.
	
	public Reporter(String report_wr_file_name, String screen_wr_file_name)
	{
		super();
		this.report_wr = make_a_file(report_wr_file_name);
		this.screen_wr = make_a_file(screen_wr_file_name);
	}

	private PrintWriter make_a_file(String file_name)
	{
		try {
			return new PrintWriter(file_name);
		} catch (FileNotFoundException e) {
			System.out.printf("Cannot make file %s.\r\n", file_name);
			System.out.println(e.getMessage());
		}
		return null;
	}

	// Obviously printing the report
	synchronized public void report(Data_Collector data_collector)
	{
		report_wr.println("###########################################################################\r\n");
		report_wr.printf("%-35s:\t%d\r\n", "NUMBER OF MESSAGES EXCHANGED",
				data_collector.get_number_of_messages_received() + data_collector.get_number_of_messages_sent());
		report_wr.printf("%-35s:\t%d\r\n", "NUMBER OF MESSAGES RECEIVED",
				data_collector.get_number_of_messages_received());
		report_wr.printf("%-35s:\t%d\r\n", "NUMBER OF MESSAGES SENT",
				data_collector.get_number_of_messages_sent());
		report_wr.printf("%-35s:\t%f\r\n", "AVERAGE LATENCY OF COMMUNICATION",
				(double)data_collector.get_total_latency()/data_collector.get_number_of_messages_received());
		report_wr.printf("%-35s:\t%f\r\n", "STANDARD DEVIATION OF LATENCIES",
				data_collector.get_latency_deviation());
		report_wr.printf("%-35s:\t%d\r\n", "NUMBER OF MESSAGES BUFFERED",
				data_collector.get_number_of_messages_buffered());
		report_wr.printf("%-35s:\t%d\r\n", "MAXIMUM BUFFERED TIME FOR A MESSAGE",
				data_collector.get_max_time_buffered());
		report_wr.printf("%-35s:\t%f\r\n", "MEAN BUFFERED TIME",
				(double)data_collector.get_total_time_buffered()/data_collector.get_number_of_messages_buffered());
		report_wr.printf("%-35s:\t%d\r\n", "MAXIMUM BUFFER SIZE DURING EXP",
				data_collector.get_buffered_peak());
		report_wr.printf("%-35s:\t%d\r\n", "TIME_AVERAGED NUMBER OF BUFFEREDS",
				data_collector.get_time_averaged_buffered());
		report_wr.printf("%-35s:\t%d\r\n", "NUMBER OF MESSAGES NOT BUFFERED",
				data_collector.get_number_of_messages_not_buffered());
		report_wr.print("\r\n###########################################################################");
		report_wr.close();
	}
	
	// Write the message to the file
	synchronized public void screen(String str)
	{
		screen_wr.println(str);
	}
	
	synchronized public void screen_pw_close()
	{
		System.out.println("All info on the screen has been written on the Screen_<NODE_ID>.TXT.");
		screen_wr.close();
	}
}
