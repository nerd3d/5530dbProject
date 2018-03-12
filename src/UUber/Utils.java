package UUber;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.Statement;

/*
 * The globally utilized class.
 */
public class Utils {

	static String currentUser = "";
	
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
	// sanitize function to be implemented..

	public enum Month {
		NUL, JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC
	};

	public class TimeData {

		public int year; // range 2018 - 9999
		public Month month; // needs to be not NUL
		public int day; // range 1 - 31 (month dependent)
		public int hour; // range 0 - 23 (represents beginning of hour block)

		public TimeData(String dateTime) {
			// Parse String into year, month, day & hour
			// call other constructor to populate fields
		}

		public TimeData(int _year, Month _month, int _day, int _hour) throws Exception {

			if (2017 < _year && _year < 9999)
				year = _year;
			else
				throw new Exception("Year is not within bounds");

			if (_month != Month.NUL)
				month = _month;
			else
				throw new Exception("Month is not valid");

			if (_day < 1)
				throw new Exception("Day is not within bounds");

			switch (month) {
			// If one of the 30 length months
			case APR:
			case JUN:
			case SEP:
			case NOV:
				if (_day > 30)
					throw new Exception("Day is not within bounds");
				break;

			// If FEB, check for leap year
			case FEB:
				if (LeapYear(year)) {
					if (_day > 29)
						throw new Exception("Day is not within bounds");
				} else {
					if (_day > 28)
						throw new Exception("Day is not within bounds");
				}
				break;

			// If else, check for 31 day month
			default:
				if (_day > 31)
					throw new Exception("Day is not within bounds");
				break;
			}
			// if you made it through that gauntlet -> day is valid!
			day = _day;
			
			if(0 <= _hour && _hour<24)
				hour = _hour;
			else
				throw new Exception("Hour is not within bounds");
		}

		private boolean LeapYear(int _year) {
			if ((_year % 4 == 0) && (_year % 100 != 0) || (_year % 400 == 0))
				return true;

			return false;
		}

	}

	/*
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
		//Handling return value:
		/*
		 * To print in columns:
		 * while (result.next()) //next row
			 {
			        resultStringBuilder+=rs.getString("user")+"   "+rs.getString("psw")+"\n"; 
			 }
			 result.close();
		 */
	}
	/*
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
		//Handling return value:
		/*
		 * To print in columns:
		 * while (result.next()) //next row
			 {
			        resultStringBuilder+=rs.getString("user")+"   "+rs.getString("psw")+"\n"; 
			 }
			 result.close();
		 */
	}
}
