package net.qyjohn.foxfs;

import java.net.*;
import java.io.*;

public class FoxServer
{
	public static void main(String[] args)
	{
		int port = Integer.parseInt(args[0]);
		FoxDBConnectionPool pool = new FoxDBConnectionPool();
        try (ServerSocket serverSocket = new ServerSocket(port)) 
		{ 
			new FoxServerKeepAliveThread(pool).start();
			while (true) 
			{
				new FoxServerThread(serverSocket.accept(), pool).start();
			}
		} catch (IOException e) 
		{
			System.err.println("Could not listen on port " + port);
			System.exit(-1);
		}
	}
}
