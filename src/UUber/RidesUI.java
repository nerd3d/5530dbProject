package UUber;

import java.sql.ResultSet;
import java.time.LocalDateTime;

public class RidesUI {
	public static void ShowMenu() throws Exception {
		while (true) {
			// welcome and list options...wait for input
			System.out.println("*** Rides ***");
			System.out.println("Please make a selection:");
			System.out.println("1. Enter a Ride Record");
			System.out.println("2. View Rides");
			System.out.println("3. Back");
			switch (Utils.getInput()) {
			case "1":
				RecordRide();
				break;
			case "2":
				DisplayRides();
				break;
			case "3":
				return;
			default:
				System.out.println("Invalid input.");
				break;
			}
		}
	}

	/*
	 * Get ride info and add record
	 */
	private static void RecordRide() throws Exception {
		String timeStr = null;
		LocalDateTime time = null;
		String carResult = null;
		float cost, distance;
		int persons;

		while (true) {
			System.out.println("*** When was the Ride? ***");
			System.out.println(" Format: YYYY-MM-DD HH ('q' to quit)");
			timeStr = Utils.getInput();
			if (timeStr == null || timeStr.equalsIgnoreCase("q"))
				return;

			try {
				time = LocalDateTime.parse(timeStr, Utils.formatINP);
			} catch (Exception e) {
				System.out.println("Failed to parse time input\n don't neglect leading zeros");
				time = null;
			}

			if (time != null) {
				// make sure time block is "in past"
				if (time.isAfter(LocalDateTime.now())) {
					System.out.println("You may not record a Ride before it happens.");
					return;
				}

				// make sure time isn't duplicated by this user
				ResultSet existingRid = Utils.QueryHelper("SELECT * FROM Ride WHERE login = '" + Utils.currentUser
						+ "' AND time = '" + time.format(Utils.formatSQL).toString() + "';", Utils.stmt);
				if (existingRid.next()) {
					System.out.println("ERROR: You already have a Ride recorded for this time slot");
					return;
				}

				carResult = VehicleBrowserUI.ShowMenu("Rides", time);
				if (carResult == null) {
					System.out.println("No valid Vehicle Chosen");
					return;
				}

				cost = -1;
				while (cost < 0) {
					System.out.print("How much did the Ride cost?: ");
					try {
						cost = Float.parseFloat(Utils.getInput());

					} catch (Exception e) {
						cost = -1;
					}

				}

				distance = -1;
				while (distance < 0) {
					System.out.print("What was the distance traveled?: ");
					try {
						distance = Float.parseFloat(Utils.getInput());
					} catch (Exception e) {
						distance = -1;
					}
				}

				persons = -1;
				while (persons < 0) {
					System.out.print("How many passengers were in the Car (including you)?: ");
					try {
						persons = Integer.parseInt(Utils.getInput());
					} catch (Exception e) {
						persons = -1;
					}
				}

				if (AddRide(carResult, time, cost, persons, distance)) {
					System.out.println("Reservation Successfully Added.");
				} else {
					System.out.println("Reservation Cancelled.");
				}

				return;
			}
		}
	}

	/*
	 * Verify user wants to add given ride to database Then -> Add the given ride to
	 * the database
	 */
	private static boolean AddRide(String vin, LocalDateTime time, float cost, int persons, float distance)
			throws Exception {
		// Read back users input
		ResultSet car = Utils.QueryHelper(
				"SELECT login, model, category FROM Car C, Owns O WHERE C.vin = O.vin AND C.vin = '" + vin + "';",
				Utils.stmt);
		if (!car.next()) {
			System.out.println("ERROR: Failed to acquire valid vin number");
			return false;
		}

		System.out.println("Time\t\tDriver\tModel\tCategory\tCost\tPassengers\tDistance");
		System.out.println("___________________________________________________________________________________");
		String disp = time.format(Utils.formatINP).toString() + "\t";
		disp += car.getString("login") + "\t";
		disp += car.getString("model") + "\t";
		disp += car.getString("category") + "\t\t";
		disp += cost + "\t";
		disp += persons + "\t\t";
		disp += distance;
		System.out.println(disp);
		System.out.println("Does this information look correct?");
		System.out.print("(y / n): ");
		String yesorno = null;
		while (yesorno == null) {
			yesorno = Utils.getInput();
			if (yesorno.equalsIgnoreCase("y")) {
				ResultSet maxRide = Utils.QueryHelper("SELECT MAX(rideid) FROM Ride;", Utils.stmt);
				int rideid = 1;
				if (maxRide.next()) {
					rideid = maxRide.getInt(1) + 1;
				}

				String addMe = "INSERT INTO Ride VALUES ( ";
				addMe += rideid + ", ";
				addMe += "'" + Utils.currentUser + "', ";
				addMe += "'" + vin + "', ";
				addMe += "'" + time.format(Utils.formatSQL) + "', ";
				addMe += cost + ", ";
				addMe += persons + ", ";
				addMe += distance + "); ";

				Utils.UpdateHelper(addMe, Utils.stmt);

				return true;
			}
			if (yesorno.equalsIgnoreCase("n")) {
				return false;
			}
			System.out.println("Please enter 'y' or 'n'");
			yesorno = null;
		}

		return false;
	}

	/*
	 * Query for rides related to user
	 */
	private static void DisplayRides() throws Exception {
		String query = "";

		query += "SELECT O.login, model, category, time, cost, num_persons, distance ";
		query += "FROM Ride R, Owns O , Car C ";
		query += "WHERE R.vin = O.vin ";
		query += "AND R.vin = C.vin ";
		query += "AND R.login = '" + Utils.currentUser + "';";

		ResultSet rides = Utils.QueryHelper(query, Utils.stmt);

		System.out.println("*** Past Ride Records ***");
		System.out.println("Time\t\tDriver\tModel\tCategory\tCost\tPassengers\tDistance");
		System.out.println("___________________________________________________________________________________");
		while (rides.next()) {
			LocalDateTime time = LocalDateTime.parse(rides.getString("time"), Utils.formatSQL);
			String disp = time.format(Utils.formatINP) + "\t";
			disp += rides.getString("O.login") + "\t";
			disp += rides.getString("model") + "\t";
			disp += rides.getString("category") + "\t\t";
			disp += rides.getString("cost") + "\t";
			disp += rides.getString("num_persons") + "\t\t";
			disp += rides.getString("distance");
			System.out.println(disp);
		}
		System.out.println("");
	}

}
