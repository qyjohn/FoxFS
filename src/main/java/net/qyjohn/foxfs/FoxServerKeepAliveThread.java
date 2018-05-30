package net.qyjohn.foxfs;

import java.net.*;
import java.sql.*;
import java.io.*;
import java.util.Properties;

public class FoxServerKeepAliveThread extends Thread
{
	String host;
	FoxDBConnectionPool pool;
	Connection conn = null;
	
	public FoxServerKeepAliveThread(FoxDBConnectionPool pool)
	{
		this.pool = pool;
		try
		{
			// Get my own IP address
			host = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		while (true)
		{
			try
			{
				keepAlive();
				sleep(15000);				
			} catch (Exception e)
			{				
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public void keepAlive()
	{
		try
		{
			conn = pool.getConnection();
			String sql = "INSERT INTO servers (host, timeline) VALUES (?, now()) ON DUPLICATE KEY UPDATE timeline=now();";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, host);
			st.executeUpdate();
			conn.close();
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();				
		}
	}
}
