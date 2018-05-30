package net.qyjohn.foxfs;

import java.net.*;
import java.io.*;
import java.security.MessageDigest;

public class FoxConfig
{
	private static final char[] hexCode = "0123456789ABCDEF".toCharArray();
	public static int FOXFS_CHUNK_SIZE = 16777216; // Default chunk size 16 MB
	
	public static String getMD5(byte[] data) 
	{
		try
		{
			byte[] md5 = MessageDigest.getInstance("MD5").digest(data);
		
			StringBuilder r = new StringBuilder(md5.length * 2);
			for (byte b : md5) 
			{
				r.append(hexCode[(b >> 4) & 0xF]);
				r.append(hexCode[(b & 0xF)]);
			}
			return r.toString();			
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		return "0123456789ABCDEF";
	}
}
