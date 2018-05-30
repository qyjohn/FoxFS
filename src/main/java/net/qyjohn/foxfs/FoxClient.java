package net.qyjohn.foxfs;

import java.net.*;
import java.io.*;
import java.util.UUID;
import java.util.Properties;

public class FoxClient
{
	public String foxfsHost;
	public int foxfsPort;
	public String[] foxfsNodes;
	
	public FoxClient()
	{
		try
		{
			// Getting database properties from db.properties
			Properties prop = new Properties();
			FileInputStream input = new FileInputStream("client.properties");
			prop.load(input);
			input.close();
			foxfsHost = prop.getProperty("foxfsHost");
			foxfsPort = Integer.parseInt(prop.getProperty("foxfsPort"));
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 *
	 * Get active FoxFS nodes and update the global variable foxfsNodes, which is a String[].
	 *
	 */
	 
	public boolean getFoxfsNodes()
	{
		byte[] command = "FOXFS00000".getBytes();
		try
		{
			// Open socket connection to the FoxFS endpoint
			Socket socket = new Socket(foxfsHost, foxfsPort);
			OutputStream out = socket.getOutputStream();
			InputStream in = socket.getInputStream();
			
			// Send the command to the FoxFS endpoint
			out.write(command);
			out.flush();
			
			// Read the response into a buffer until the server side actively close the connection.
			// The response contains a list of FoxFS nodes, one at a line
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();			
			int nRead;
			byte[] data = new byte[16384]; // 16 KB in each read
			while ((nRead = in.read(data, 0, data.length)) != -1) 
			{
				buffer.write(data, 0, nRead);
			}
			buffer.flush();
			
			// Here we have a list of FoxFS nodes
			foxfsNodes = new String(buffer.toByteArray()).trim().split("\n");
			
			// Close the I/O channel
			in.close();
			out.close();
			socket.close();
		} catch (Exception e)
		{
			
		}
		return true;
	}
	
	
	public boolean putObject(String localFile, String foxfsFile)
	{
		try
		{
			// 0 - Get active FoxFS nodes
			getFoxfsNodes();
			
			// 1 - Make sure the file exists
			File file = new File(localFile);
			if(file.exists() && !file.isDirectory()) 
			{
				// Calculate how many parts is needed
				long length = file.length();
				int  parts  = (int) ((length + FoxConfig.FOXFS_CHUNK_SIZE - 1) / FoxConfig.FOXFS_CHUNK_SIZE);
				System.out.println("Local file: " + localFile + ", " + length + " bytes, " + parts + " parts.");
			
				// Create an array of buffers to store the 
				byte[][]data = new byte[parts][];
				int[] lengths = new int[parts];
				String[] md5s = new String[parts];
				String[] uuid = new String[parts];				
				for (int i=0; i<(parts-1); i++)
				{
					data[i] = new byte[FoxConfig.FOXFS_CHUNK_SIZE];
				}
				int remain = (int) (length % FoxConfig.FOXFS_CHUNK_SIZE);
				data[parts-1] = new byte[remain];
				
				// Read the content of the file into the buffer, also calculate 
				FileInputStream fio = new FileInputStream(file);
				for (int i=0; i<parts; i++)
				{
					fio.read(data[i]);
					lengths[i] = data[i].length;
					md5s[i] = FoxConfig.getMD5(data[i]);
					uuid[i] = UUID.randomUUID().toString();
					System.out.println("Part " + i + "\t UUID " + uuid[i] + ", length " + lengths[i] + " bytes \t MD5: " + md5s[i]);
				}
				fio.close();
				
				// Put object meta data first
				
				// Upload parts one by one
				
				
			}
			else
			{
				System.out.println("The local file specified does not exist.");
				return false;
			}
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		return true;		
	}
	
	
	public boolean getObject(String localFile, String foxfsFile)
	{
		return true;
	}
	
	
	public void printUsage()
	{
		System.out.println("Usage: ");
		System.out.println("  java FoxClient put localFile foxfsFile");	
		System.out.println("  java FoxClient get foxfsFile localFile");	
	}
	
	public static void main(String[] args)
	{
		FoxClient client = new FoxClient();
		try
		{
			if (args[0].equals("put"))
			{
				client.putObject(args[1], args[2]);
			}
			else if (args[0].equals("get"))
			{
				client.getObject(args[1], args[2]);
			}
			else
			{
				client.printUsage();	
			}
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
