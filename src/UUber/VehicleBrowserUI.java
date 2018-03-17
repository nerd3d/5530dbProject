package UUber;

import java.sql.*;
import java.time.*;

public class VehicleBrowserUI {

	// returns the vin number of a selected vehicle. null if selection is canceled
	public static String ShowMenu() throws Exception{
		String query = "";
		ResultSet result = null;
		
		while (true) {
			// welcome and list options...wait for input
			System.out.println("*** Vehicles ***");
			System.out.println("Num\tVIN\tDriver");
			query += "SELECT vin, name ";
			query += "FROM Owns, User ";
			query += "WHERE Owns.login = User.login ";
			query += "ORDER BY name ";
			
			result = Utils.QueryHelper(query, Utils.stmt);
			int row = 1;
			while(result.next()) {
				System.out.print(row+"\t");
				System.out.print(result.getString("vin")+"\t");
				System.out.print(result.getString("name") + "\n");
				
				row++;
			}
			
			System.out.println("\n<press any key to go back>"); // replace with selection options
			Utils.getInput();
			return null; // remove this when implemented!
		}
	}

	// returns the vin number of a selected vehicle. null if selection is canceled
	public static String ShowMenu(LocalDateTime time) throws Exception {
		
		while (true) {
			// welcome and list options...wait for input
			System.out.println("*** Vehicles available during "+ time.format(Utils.formatSQL).toString() +" ***");
			System.out.println("Show a bunch of cars...");
			System.out.println("need filter options etc...");
			System.out.println("<press any key to go back>");
			
			Utils.getInput();
			return null; // Needs to return the Car vin
		}
	}

}
