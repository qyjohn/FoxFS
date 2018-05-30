package net.qyjohn.foxfs;

import java.net.*;
import java.io.*;
import java.sql.*;

public class FoxServerThread extends Thread
{
	private Socket socket = null;
	private FoxDBConnectionPool pool;
	private InputStream in;
	private OutputStream out;
 
	public FoxServerThread(Socket socket, FoxDBConnectionPool pool) 
	{
		try
		{
			this.socket = socket;
			this.pool = pool;
			in  = socket.getInputStream();
			out = socket.getOutputStream();
		} catch (Exception e)
		{
			
		}
	}
     
	public void run() 
	{
		try
		{
			// When the connection is established, the client side actively sends a command code to 
			// indicate the purpose of this connections. The command code has 10 bytes in total.
			byte[] code = new byte[10];
			in.read(code);
			String command = new String(code);
			System.out.println(command);
			
			if (command.equals("FOXFS00000"))
			{
				processGetNodes();
			}
			
			in.close();
			out.close();
			socket.close();
		} catch (Exception e)
		{
			
		}
    }
    
    
    // Command FOXFS00000 - GetServerList
    public boolean processGetNodes()
    {
		try
		{
			// Query the DB for the recently active nodes
			Connection conn = pool.getConnection();			
			String sql = "SELECT * FROM servers WHERE timeline > DATE_SUB(NOW(),INTERVAL 20 SECOND) ORDER BY timeline DESC";
			Statement statement = conn.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			if (resultSet.next())
			{
				String host = resultSet.getString("host") + "\n";
				out.write(host.getBytes());
			}
			out.flush();
			conn.close();
		} catch (Exception e)
		{
			
		}
		return true;
    }
    

}
