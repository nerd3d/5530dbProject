package UUber;

import java.lang.*;
import java.sql.*;
import java.io.*;

public class StartPoint {

	static Connector connect = null;

	public static void main(String[] args) {

		try {
			ConnectToDB();
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
			Utils.stmt = connect.st;
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
	}
}
