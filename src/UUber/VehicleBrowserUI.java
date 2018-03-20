package UUber;

import java.sql.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

//asks for filters
//asks for sorting
//adjust query depending on caller (reserve, rides, mainmenu)
//Displays list of cars

public class VehicleBrowserUI {
	
	
	
	// returns the vin number of a selected vehicle. null if selection is canceled
	public static String ShowMenu(String caller, LocalDateTime time) throws Exception{
		String query = "";
		ResultSet result = null;
		
		/////////////////////////////////////////////////////////////////////////
		//asks for filters
		/////////////////////////////////////////////////////////////////////////
		System.out.println("Choose filters by typing one of the offered numbers:");
		//ask for category
		System.out.println("Vehicle Category: (1) No filter (2) SUV (3) Truck (4) Sedan (5) Economy (6) Comfort (7) Luxury");
		int cat = Integer.parseInt(Utils.getInput());
		//if invalid input
		if(cat < 1 || cat > 7) {
			System.out.println("Invalid input.");
			return null;
		}

		//ask for address (state)
		System.out.println("Please provide desired state (example: Texas) or skip");
		String state = Utils.getInputToLower();
		if(state != null && !state.equals(""))
		{
			//sanitize
			if(!Utils.SanitizeInput(state, "[a-zA-Z ]{4,13}")) {
				System.out.println("State needs to be from 4 - 13 characters long, Letters and spaces only.");
				return null;
			}
			//add to query: where state = state
		}
		//ask for address (city)
		System.out.println("Please provide desired city (example: Atlanta) or skip");
		String city = Utils.getInputToLower();
		if(city != null && !city.equals(""))
		{
			//sanitize
			if(!Utils.SanitizeInput(city, "[a-zA-Z ]{1,30}")) {
				System.out.println("City needs to be from 1 - 30 characters long, Letters and spaces only.");
				return null;
			}
			//add to query: where city = city
		}
		//ask for model keyword
		System.out.println("Please provide model keyword (example: BMW) or skip");
		String keyword = Utils.getInputToLower();
		if(keyword != null && !keyword.equals(""))
		{
			//sanitize
			if(!Utils.SanitizeInput(keyword, "[a-zA-Z0-9 ]{1,30}")) {
				System.out.println("Model keyword needs to be from 1 - 10 characters long, Letters, numbers and spaces only.");
				return null;
			}
			//add to query: where model like keyword
		}
		
		/////////////////////////////////////////////////////////////////////////
		//asks for sorting
		/////////////////////////////////////////////////////////////////////////
		//specify sorting: (average feedback score, average trusted feedback score)
		System.out.println("Sort by average feedback score overall (1) or according to trusted users (2) or no sort (3)");
		int sort = Integer.parseInt(Utils.getInput());
		//if invalid input
		if(cat < 1 || cat > 3) {
			System.out.println("Invalid input.");
			return null;
		}
		//update query based off sort
		
		
		/////////////////////////////////////////////////////////////////////////
		//adjust query based on caller
		/////////////////////////////////////////////////////////////////////////
		switch(caller) {
		case "Ride":
			//additional filter of only cars available right now
			//selecting a car will give menu of
			//record ride >> returns vin
			//give feedback
			//view feedback
			//back to vehicle browser
			//exit vehicle browser
			break;
		case "Reservation":
			//any additional filter for reservation?
			//selecting a car will give menu of
			//set reservation >> returns vin
			//give feedback
			//view feedback
			//back to vehicle browser
			//exit vehicle browser
			break;
		case "MainMenu":
			//selecting a car will give menu of
			//give feedback
			//view feedback
			//back to vehicle browser
			//exit vehicle browser
			break;
		default:
			return null;
		}
		while (true) {
			List<String> vinList = new ArrayList<String>();
			// welcome and list options...wait for input
			//System.out.println("*** Vehicles ***");
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
				vinList.add(result.getString("vin"));
				row++;
			}
			System.out.println("Select a vehicle by the left-most number.");
			int selected = Integer.parseInt(Utils.getInput());
			if(selected < 1 || selected > vinList.size())
			{
				System.out.println("Invalid selection.");
				return null;
			}
			return vinList.get(selected-1);
			//System.out.println("\n<press any key to go back>"); // replace with selection options
			//Utils.getInput();
			//return null; // remove this when implemented!
		}
	}

	/*// returns the vin number of a selected vehicle. null if selection is canceled
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
	}*/

}
