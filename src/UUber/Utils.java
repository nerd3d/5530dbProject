package UUber;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.regex.*;

/*
 * The globally utilized class.
 */
public class Utils {
	static Statement stmt;
	static String currentUser = "";
	static DateTimeFormatter formatINP = DateTimeFormatter.ofPattern("uuuu-MM-dd HH");
	static DateTimeFormatter formatSQL = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
	
	public static String getInput() throws Exception {
		String str;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while ((str = in.readLine()) == null && str.length() == 0)
			;
		return str;
	}

	public static String getInputToLower() throws Exception {
		String str;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while ((str = in.readLine()) == null && str.length() == 0)
			;
		return str.toLowerCase();
	}

	public static String getInputToUpper() throws Exception {
		String str;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while ((str = in.readLine()) == null && str.length() == 0)
			;
		return str.toUpperCase();
	}

	public static String SanitizeInput(String value, String regx) throws Exception{
		String result = value;
		boolean inputOK = Pattern.matches( regx, value); 
		
		if(!inputOK)
			throw new Exception("Invalid input character");
		
		return result;
	}

	/*Executes sql query
	 * If null return value, query failed. Please see comments at bottom for handling return value.
	 */
	public static ResultSet QueryHelper(String query, Statement stmt) throws Exception{
		ResultSet result;
		System.out.println("executing query: "+query);
	 	try{
		 	result=stmt.executeQuery(query);
	 	}
	 	catch(Exception e)
	 	{
	 		System.out.println("query failed to execute");
	 		result = null;
	 	}
		return result;
		
	}
	/*Use for INSERT, UPDATE or DELETE
	 * If -1 return value, error occurred. 0 means no rows changed. 1 means row changed.
	 * 
	 */
	public static int UpdateHelper(String query, Statement stmt) throws Exception{
		int result;
		System.out.println("executing query: "+query);
	 	try{
		 	result=stmt.executeUpdate(query);
	 	}
	 	catch(Exception e)
	 	{
	 		System.out.println("query failed to execute");
	 		result = -1;
	 	}
		return result;
		
	}
}
