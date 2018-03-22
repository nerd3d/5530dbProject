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
		String filter = "";
		String fromOrder = "";
		String sortStr = "";
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
		filter = catStringToQuery(cat);

		//ask for address (state)
		System.out.println("Please provide desired state (example: Texas) or skip");
		String state = Utils.getInputToLower();
		if(state != null && !state.equals(""))
		{
			//sanitize
			if(!Utils.SanitizeInput(state, "[a-zA-Z ]{4,14}")) {
				System.out.println("State needs to be from 4 - 14 characters long, Letters and spaces only.");
				return null;
			}
			//add to query: where state = state
			filter += " AND User.state = '" + state + "'";
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
			filter += " AND User.city = '" + city + "'";
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
			filter += " AND C.model like '%" + keyword + "%'";
		}
		
		/////////////////////////////////////////////////////////////////////////
		//asks for sorting
		/////////////////////////////////////////////////////////////////////////
		//specify sorting: (average feedback score, average trusted feedback score)
		System.out.println("Sort by average feedback score (1) or according to trusted users (2) or no sort (3)");
		int sort = Integer.parseInt(Utils.getInput());
		//if invalid input
		if(sort < 1 || sort > 3) {
			System.out.println("Invalid input.");
			return null;
		}
		//update query based off sort
		if(sort != 3)
		{
			fromOrder = "";//create the new table that the main query will join with in order to ORDER BY avg rating
			if(sort == 1)
				//FEEDBACK PROBABLY NEEDS TO BE ON IT's OWN QUERY SO WE DON'T RETURN DUPLICATES
				// ORDER BY avgRating desc 
				fromOrder = "(SELECT vin, AVG(rating) avgRating FROM Feedback GROUP BY vin) F ";//" ORDER BY avg(F.rating) desc";//avg feedback score
			else
				fromOrder = "(SELECT F1.vin, AVG(F1.rating) avgRating FROM Feedback F1,Trust T1 WHERE T1.login = '"+Utils.currentUser+"' AND F1.login = T1.login2 GROUP BY F1.vin) F "; // avg trusted feedback score
		}
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
			//favorite
			//back to vehicle browser
			break;
		case "Reservation":
			//any additional filter for reservation?
			//selecting a car will give menu of
			//set reservation >> returns vin
			//give feedback
			//view feedback
			//favorite
			//back to vehicle browser
			break;
		case "MainMenu":
			//selecting a car will give menu of
			//give feedback
			//view feedback
			//favorite
			//back to vehicle browser
			break;
		default:
			return null;
		}
		while (true) {
			List<String> vinList = new ArrayList<String>();
			// welcome and list options...wait for input
			//System.out.println("*** Vehicles ***");
			System.out.println("Num\tVIN\tDriver");
			query += "SELECT Owns.vin, User.name ";
			query += "FROM Owns, User, Car C ";//, Feedback F
			if(!fromOrder.equals(""))
			{
				query += ", " + fromOrder;
			}
			query += "WHERE Owns.login = User.login  AND Owns.vin = C.vin " + filter;
			if(!fromOrder.equals(""))
				query += " AND F.vin = C.vin";
			if(sort != 3) {//if special order by...
				query += " ORDER BY F.avgRating desc " ;
			}
			else
				query += " ORDER BY name ";
			
			while(true)
			{
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
			System.out.println("Or, type nothing and hit enter to quit vehicle browser.");
			String in = Utils.getInput();
			if(in == null || in.equals(""))
				return null;
			try{
				int selected = Integer.parseInt(in);
				if(selected < 1 || selected > vinList.size())
				{
					System.out.println("Invalid selection.");
					return null;
				}
				String selectedVin = vinList.get(selected-1);
				System.out.println("Got vin: "+ selectedVin);
				//Selected car menu:
				System.out.println("Choose an option by its number.");
				//1. give feedback
				System.out.println("1. View feedback");
				//2. view feedback
				System.out.println("2. Give feedback");
				//3. favorite
				System.out.println("3. Favorite this car");
				//4. unfavorite
				System.out.println("4. Un-Favorite this car");
				//5. back to car list
				System.out.println("5. Back");
															//if some selects "ride or reserve": 	
															//return vinList.get(selected-1);
				String in1 = Utils.getInput();
				if(in1 == null || in1.equals(""))
				{
					System.out.println("Invalid input.");
					return null;
				}
				try {
					int in1Int = Integer.parseInt(in1);
					switch (in1Int){
					case 1:
						break;
					case 2:
						break;
					case 3://add favorite
						System.out.println("Vehicle added to favorites.");
						Utils.UpdateHelper("INSERT IGNORE INTO Favorite VALUES('"+Utils.currentUser+"', '"+selectedVin+"');", Utils.stmt);//favorite a car
						break;
					case 4://remove favorite
						System.out.println("Vehicle removed from favorites.");
						Utils.UpdateHelper("DELETE FROM Favorite WHERE login = '"+Utils.currentUser+"' AND vin = '"+selectedVin+"';", Utils.stmt);//favorite a car
						break;
					case 5:
						break;//continue to start of while loop (redraws car list)
					default:
						System.out.println("Invalid input. Returning to vehicle browser.");
						break;
					}
				}
				catch(Exception e) {System.out.println("Invalid selection.");return null;}
			}
			catch(Exception e) {System.out.println("Invalid selection.");return null;}
			//System.out.println("\n<press any key to go back>"); // replace with selection options
			//Utils.getInput();
			//return null; // remove this when implemented!
			}
		}
	}

	
	private static String catStringToQuery(int i){
		String r = "";
		//(1) No filter (2) SUV (3) Truck (4) Sedan (5) Economy (6) Comfort (7) Luxury
		switch(i) {
		case 2:
			r = "AND C.category = 'suv'";
			break;
		case 3:
			r = "AND C.category = 'truck'";
			break;
		case 4:
			r = "AND C.category = 'sedan'";
			break;
		case 5:
			r = "AND C.category = 'economy'";
			break;
		case 6:
			r = "AND C.category = 'comfort'";
			break;
		case 7:
			r = "AND C.category = 'luxury'";
			break;
		}
		return r;
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
