package UUber;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * Connector handles the connection to the database.
 **/
class Connector {
	public Statement st = null; // used to prepare and issue statements to the database
	public Connection con = null; // maintains the connection to the database
	public Session sesh = null; // maintains the connection to the host
	
	/**
	 * Instantiates a new Connector Object. Creates a connection to the host and
	 * 	logs into the database
	 **/
	public Connector(String sshUser, String sshPassword) throws Exception {
		String sshHost = "georgia.eng.utah.edu";
		
		int lport = 5656;
		String rhost = "localhost";
		int rport = 3306;

		String url = "jdbc:mysql://localhost:" + lport + "/5530u21?allowMultiQueries=true";
		String dbUser = "5530u21";
		String dbPassword = "va9vg8h";
		String driverName = "com.mysql.jdbc.Driver";
		
		try 
		{
			// modify configuration: remove strict host checking
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			// create a new JSch object
			JSch jsch = new JSch();
			
			// populate session field
			sesh = jsch.getSession(sshUser, sshHost, 22);
			sesh.setPassword(sshPassword);
			sesh.setConfig(config);
			sesh.connect(); // connect to host
			System.out.println("Connected");
			
			// set port forwarding
			int aport = sesh.setPortForwardingL(lport, rhost, rport);
			System.out.println("localhost:" + aport + " -> " + rhost + ":" + rport);
			System.out.println("Port Forwarded");
			
			// Connect to database
			Class.forName(driverName).newInstance();
			con = DriverManager.getConnection(url, dbUser, dbPassword);
			st = con.createStatement();
			
			System.out.println("Database connection established");
			System.out.println("DONE");
		}
		catch (SQLException sql)
		{
			sql.printStackTrace();
			throw sql;
		}
		
	}
	
	public void CloseConnection() throws Exception 
	{
		con.close();
		sesh.disconnect();
	}
}
