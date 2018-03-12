package UUber;

import java.lang.*;
import java.sql.*;
import java.io.*;

public class StartPoint {

	static Connector connect = null;

	public static void main(String[] args) {

		try {
			ConnectToDB();
			//LoginUI.ShowMenu();
		} catch (Exception e) {
			
		}
	}

	/* connects to database then launches user experience -> Login Menu 
	 * Upon User Exit, the connection is closed properly.
	 * */
	private static void ConnectToDB() throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String login;
		String pass;
		System.out.println("Welcome to UUber database!");
		System.out.println("Please enter Host Login Name:");
		while ((login = in.readLine()) == null && login.length() == 0);
		System.out.println("Please enter Host Password:");

		Console c = System.console();
		if (c != null) {
			// Console supports hidden passwords. Get the password.
			char[] pw = System.console().readPassword();
			pass = new String(pw);

		} else {
			// Not in a legit Console, get the password in plain text.
			while ((pass = in.readLine()) == null && pass.length() == 0);
		}
		
		try {
			connect = new Connector(login, pass);
			
			// Connection Established -> proceed to Login Menu
			LoginUI.ShowMenu();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			// before exiting program -> close connection properly
			if (connect != null) {
				try {
					connect.CloseConnection();
					System.out.println("Database connection terminated");
				}
				catch (Exception e) {
					/* ignore close errors */ 
				}
			}
		}
		/*String query = "SHOW TABLES";
		ResultSet result;
		String resultStr = "";
		try{
			result = connect.st.executeQuery(query);	
        } catch(Exception e) {
			System.err.println("Unable to execute query:"+query+"\n");
	        System.err.println(e.getMessage());
			throw(e);
		}
		
		System.out.println("Available Tables="+query+"\n");
		while (result.next()){
			resultStr += result.getString("Tables_in_5530db21")+"\n";	
		}

		System.out.println("Tables_in_5530db21\n"+resultStr);*/
	}
}
