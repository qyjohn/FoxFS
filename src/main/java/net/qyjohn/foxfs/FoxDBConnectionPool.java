package net.qyjohn.foxfs;

import java.util.Properties;
import java.io.FileInputStream;
import java.sql.Connection;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

public class FoxDBConnectionPool
{
	private String dbHostname, dbUsername, dbPassword, dbDatabase, jdbcUrl;
	BoneCP connectionPool = null;
	
	public FoxDBConnectionPool()
	{
		try
		{
			// Load the MySQL JDBC driver
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			// Getting database properties from db.properties
			Properties prop = new Properties();
			FileInputStream input = new FileInputStream("server.properties");
			prop.load(input);
			input.close();
			dbHostname = prop.getProperty("dbHostname");
			dbUsername = prop.getProperty("dbUsername");
			dbPassword = prop.getProperty("dbPassword");
			dbDatabase = prop.getProperty("dbDatabase");			
			jdbcUrl = "jdbc:mysql://" + dbHostname + "/" + dbDatabase;			
			
			BoneCPConfig config = new BoneCPConfig();
			config.setJdbcUrl(jdbcUrl);
			config.setUsername(dbUsername); 
			config.setPassword(dbPassword);
			config.setMinConnectionsPerPartition(5);
			config.setMaxConnectionsPerPartition(20);
			config.setPartitionCount(1);
			connectionPool = new BoneCP(config); // setup the connection pool	
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public Connection getConnection()
	{
		try
		{
			return connectionPool.getConnection();		
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

}
