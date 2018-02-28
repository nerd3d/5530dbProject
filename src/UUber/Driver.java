package UUber;

import java.lang.*;
import java.sql.*;
import java.io.*;

public class Driver {

	static Connector connect = null;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hello World");

		try {
			ConnectToDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void ConnectToDB() throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String login;
		String pass;
		System.out.println("Welcome to UUber database!");
		System.out.println("Please enter Host Login Name:");
		while ((login = in.readLine()) == null && login.length() == 0);
		System.out.println("Please enter Host Password:");
		while ((pass = in.readLine()) == null && pass.length() == 0);

		try {
			connect = new Connector(login, pass);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (connect != null) {
				try {
					connect.CloseConnection();
					System.out.println("Database connection terminated");
				}

				catch (Exception e) {
					/* ignore close errors */ }
			}
		}
	}
}
