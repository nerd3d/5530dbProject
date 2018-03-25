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
			System.out.println("4. User Separation");
			System.out.println("5. Back");
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
				Separation();
				break;
			case "5":
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

	private static void PopularDrivers() throws Exception {
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
			System.out.println("Driver\tAverage Rating");
			System.out.println("--------------------------------------------------------");
			String query = "SELECT O.login, rate FROM Car C, Owns O, "
					+ "(SELECT O.login, AVG(rating) rate FROM Owns O, Feedback F WHERE O.vin = F.vin GROUP BY O.login) R "
					+ "WHERE C.vin = O.vin AND O.login = R.login AND C.category = '" + cat
					+ "' ORDER BY rate DESC LIMIT " + limit + ";";
			result = Utils.QueryHelper(query, Utils.stmt);

			while (result.next()) {
				String disp = result.getString("login") + "\t";
				disp += result.getString("rate");

				System.out.println(disp);
			}
			System.out.println("--------------------------------------------------------\n");
		}

		System.out.println("<Press Enter to Return>");
		Utils.getInput();

	}

	/*
	 * Asks for 2 user names and determines how similar their 'favorites' are
	 */
	private static void Separation() throws Exception {
		ResultSet result;

		System.out.print("Enter an UUber User Name: ");
		String userA = Utils.getInputToLower();
		if (!Utils.SanitizeInput(userA, "[a-zA-Z]{1}[a-zA-Z0-9]{3,19}")) {
			{
				System.out.println("Input error. \nUsername needs to start with a letter.");
				System.out.println("Username length needs to be from 4 - 20 characters long");
				return;
			}
		}
		result = Utils.QueryHelper("SELECT login FROM User WHERE login = '" + userA + "';", Utils.stmt);
		if (!result.next()) {
			System.out.println(userA + " is not a current UUber User.");
			return;
		}

		System.out.print("Enter a different UUber User Name: ");
		String userB = Utils.getInputToLower();
		if (!Utils.SanitizeInput(userB, "[a-zA-Z]{1}[a-zA-Z0-9]{3,19}")) {
			{
				System.out.println("Input error. \nUsername needs to start with a letter.");
				System.out.println("Username length needs to be from 4 - 20 characters long");
				return;
			}
		}
		result = Utils.QueryHelper("SELECT login FROM User WHERE login = '" + userB + "';", Utils.stmt);
		if (!result.next()) {
			System.out.println(userB + " is not a current UUber User.");
			return;
		}

		if (userA.equalsIgnoreCase(userB)) {
			System.out.println("\nUser names must be different.\n");
			return;
		}

		// Determine the 1-degree separation for User A
		ArrayList<String> userAOne = GetOneDegree(userA);
		if (userAOne.contains(userB)) {
			System.out.println("\n" + userA + " and " + userB + " are 1 Degree Separated.\n");
			return;
		}

		// Determine the 1-degree separation for User A
		ArrayList<String> userBOne = GetOneDegree(userB);
		for (String u : userAOne) {
			if (userBOne.contains(u)) {
				System.out.println("\n" + userA + " and " + userB + " are 2 Degree Separated.\n");
				return;
			}
		}

		System.out.println("\n" + userA + " and " + userB + " are not related or are more than 2 Degree Separated.\n");
		return;
	}

	// gets all the people who've shared in a common favorite car
	private static ArrayList<String> GetOneDegree(String user) throws Exception {
		ArrayList<String> otherUsers = new ArrayList<String>();

		String query = "SELECT F2.login user FROM Favorite F1, Favorite F2 WHERE F1.login = '" + user
				+ "' AND F1.login <> F2.login AND F1.vin = F2.vin;";
		ResultSet result = Utils.QueryHelper(query, Utils.stmt);

		while (result.next()) {
			otherUsers.add(result.getString("user"));
		}

		return otherUsers;
	}

}
