package UUber;

import java.sql.ResultSet;
import java.util.ArrayList;

public class StatisticsUI {
	public static void ShowMenu() throws Exception {
		while (true) {
			// welcome and list options...wait for input
			System.out.println("*** UUber Statistics ***");
			System.out.println("Please make a selection:");
			System.out.println("1. Most Popular Cars");
			System.out.println("2. Most Expensive Cars");
			System.out.println("3. Most Highly Rated Drivers");
			System.out.println("4. Back");
			switch (Utils.getInput()) {
			case "1":
				PopularCars();
				break;
			case "2":
				ExpensiveCars();
				break;
			case "3":
				PopularDrivers();
				break;
			case "4":
				return;
			default:
				System.out.println("Invalid input.");
				return;
			}
		}
	}

	private static void PopularCars() throws Exception {
		System.out.print("Limit results per category to (default 5): ");
		int limit = 5;
		boolean valid = false;
		while (!valid) {
			try {
				String lim = Utils.getInput();
				if (lim.isEmpty())
					break;
				limit = Integer.parseInt(lim);
				valid = true;
			} catch (Exception e) {
				System.out.println("ERROR: Please enter a single integer");
			}
		}

		ArrayList<String> categories = new ArrayList<String>();
		ResultSet result = Utils.QueryHelper("SELECT * FROM Category;", Utils.stmt);
		while (result.next()) {
			categories.add(result.getString("category"));
		}

		for (String cat : categories) {
			System.out.println("Category = " + cat);
			System.out.println("Driver\tYear\tMake\tModel\tNumber of Rides");
			System.out.println("--------------------------------------------------------");
			String query = "SELECT login, year, make, model, rides " + "FROM Car C, Owns O, "
					+ "(SELECT vin, COUNT(vin) rides FROM Ride GROUP BY vin) R "
					+ "WHERE C.vin = O.vin AND C.vin = R.vin " + "AND category = '" + cat + "'"
					+ "ORDER BY rides DESC LIMIT " + limit + ";";
			result = Utils.QueryHelper(query, Utils.stmt);

			while (result.next()) {
				String disp = result.getString("login") + "\t";
				disp += result.getString("year") + "\t";
				disp += result.getString("make") + "\t";
				disp += result.getString("model") + "\t";
				disp += result.getString("rides");
				
				System.out.println(disp);
			}
			System.out.println("--------------------------------------------------------\n");
		}
		
		System.out.println("<Press Enter to Return>");
		Utils.getInput();
	}

	private static void ExpensiveCars() throws Exception {
		System.out.print("Limit results per category to (default 5): ");
		int limit = 5;
		boolean valid = false;
		while (!valid) {
			try {
				String lim = Utils.getInput();
				if (lim.isEmpty())
					break;
				limit = Integer.parseInt(lim);
				valid = true;
			} catch (Exception e) {
				System.out.println("ERROR: Please enter a single integer");
			}
		}

		ArrayList<String> categories = new ArrayList<String>();
		ResultSet result = Utils.QueryHelper("SELECT * FROM Category;", Utils.stmt);
		while (result.next()) {
			categories.add(result.getString("category"));
		}

		for (String cat : categories) {
			System.out.println("Category = " + cat);
			System.out.println("Driver\tYear\tMake\tModel\tAverage Cost");
			System.out.println("--------------------------------------------------------");
			String query = "SELECT login, year, make, model, average " + "FROM Car C, Owns O, "
					+ "(SELECT vin, AVG(cost) average FROM Ride GROUP BY vin) R "
					+ "WHERE C.vin = O.vin AND C.vin = R.vin " + "AND category = '" + cat + "'"
					+ "ORDER BY average DESC LIMIT " + limit + ";";
			result = Utils.QueryHelper(query, Utils.stmt);

			while (result.next()) {
				String disp = result.getString("login") + "\t";
				disp += result.getString("year") + "\t";
				disp += result.getString("make") + "\t";
				disp += result.getString("model") + "\t";
				disp += result.getString("average");
				
				System.out.println(disp);
			}
			System.out.println("--------------------------------------------------------\n");
		}
		
		System.out.println("<Press Enter to Return>");
		Utils.getInput();
	}

	private static void PopularDrivers() {
		

	}
}
