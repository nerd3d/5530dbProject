package UUber;

import java.sql.ResultSet;
import java.time.*;
import java.time.format.*;

public class ReservationUI {
	public static void ShowMenu() throws Exception {
		while (true) {
			// welcome and list options...wait for input
			System.out.println("*** Reservations ***");
			System.out.println("Please make a selection:");
			System.out.println("1. Reserve a Car");
			System.out.println("2. View Reservations");
			System.out.println("3. Back");
			switch (Utils.getInput()) {
			case "1":
				ReserveCar();
				break;
			case "2":
				DisplayReservations();
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
	 * Browse or Search for an available car, add a reservation
	 */
	private static void ReserveCar() throws Exception {
		String timeStr = null;
		LocalDateTime time = null;
		String carResult = null;

		while (true) {
			System.out.println("*** Select a Time ***");
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
				// make sure time isn't duplicated by this user
				ResultSet existingRes = Utils.QueryHelper("SELECT * FROM Reservation WHERE login = '"
						+ Utils.currentUser + "' AND time = '" + time.format(Utils.formatSQL).toString() + "';",
						Utils.stmt);
				if (existingRes.next()) {
					System.out.println("ERROR: You already have a reservation for this time slot");
					return;
				}

				carResult = VehicleBrowserUI.ShowMenu("Reservation", time);
				if (carResult == null) {
					System.out.println("No valid Vehicle Chosen");
					return;
				}

				if (AddReservation(carResult, time)) {
					System.out.println("Reservation Successfully Added.");

					// Suggest similar cars for future rides
					SuggestCars(carResult);

				} else {
					System.out.println("Reservation Cancelled.");
				}

				return;
			}
		}
	}

	/*
	 * Given a Car VIN number: List other cars ridden by a user that also rode this
	 * car. Sort by most popular (number of rides by given user)
	 */
	private static void SuggestCars(String vin) throws Exception {
		System.out.println("Great selection! May we also recomend these other popular cars for future trips?");

		System.out.println("Driver\tCategory\tYear\tModel");

		// List of Vins also ridden by users that rode this car
		String otherCars = "SELECT R.vin, COUNT(R.vin) rides FROM Ride R, Ride X "
				+ "WHERE R.vin <> X.vin AND X.vin = '" + vin + "' AND R.login=X.login GROUP BY R.vin";

		// driver and Car data for the 'otherCar' list
		String carData = "SELECT O.login, category, year, model, rides FROM Car C, Owns O, " + "(" + otherCars
				+ ") S WHERE S.vin = C.vin AND C.vin = O.vin ORDER BY rides DESC;";

		ResultSet recomend = Utils.QueryHelper(carData, Utils.stmt);
		while (recomend.next()) {
			String disp = recomend.getString("login") + "\t";
			disp += recomend.getString("category") + "\t";
			disp += recomend.getString("year") + "\t";
			disp += recomend.getString("model") + "\t";
			System.out.println(disp);
		}
		
	}

	/*
	 * Generate, modify and display Reservation related queries
	 */
	private static void DisplayReservations() throws Exception {
		String query = "";

		query += "SELECT resid, O.login, category, model, time ";
		query += "FROM Reservation R, Car C, Owns O ";
		query += "WHERE time > NOW() ";
		query += "AND R.vin = C.vin ";
		query += "AND O.vin = C.vin ";
		query += "AND R.login = '" + Utils.currentUser + "';";

		System.out.println("*** Future Reservations ***");
		ResultSet reservations = Utils.QueryHelper(query, Utils.stmt);
		System.out.println("NUM\tDriver\tCategory\tModel\tTime");
		System.out.println("___________________________________________________________________");

		int num = 1;
		while (reservations.next()) {
			System.out.print(num + "\t");
			System.out.print(reservations.getString("O.login") + "\t");
			System.out.print(reservations.getString("category") + "\t");
			System.out.print(reservations.getString("model") + "\t");
			System.out.print(reservations.getString("time") + "\n");
			num++;
		}

		System.out.println("Select resevation to remove OR press enter to return");
		String exit = Utils.getInput();
		if (exit.equalsIgnoreCase(""))
			return;

		num = Integer.parseInt(exit);
		reservations.last();
		if (num > 0 && num <= reservations.getRow()) {
			reservations.beforeFirst();
			reservations.relative(num);
			int deleteMe = reservations.getInt("resid");
			System.out.print(num + "\t");
			System.out.print(reservations.getString("O.login") + "\t");
			System.out.print(reservations.getString("category") + "\t");
			System.out.print(reservations.getString("model") + "\t");
			System.out.print(reservations.getString("time") + "\n");
			System.out.println("Are you sure you want to cancel this reservation?");
			System.out.print("(y / n): ");
			String yesorno = null;
			while (yesorno == null) {
				yesorno = Utils.getInput();
				if (yesorno.equalsIgnoreCase("y")) {
					Utils.UpdateHelper("DELETE FROM Reservation WHERE resid = " + deleteMe + ";", Utils.stmt);
					System.out.println("Reservation Removed");
					return;
				}
				if (yesorno.equalsIgnoreCase("n")) {
					System.out.println("Aborting Cancel.. returning to main menu.");
					return;
				}
				System.out.println("Please enter 'y' or 'n'");
				yesorno = null;
			}
		} else {
			System.out.println("ERROR: expecting integer within range");
		}
	}

	/*
	 * Attempt to Add a reservation to the database. Return true if successful
	 */
	private static boolean AddReservation(String vin, LocalDateTime time) throws Exception {
		// Display reservation info for verification
		String query = "SELECT * FROM Car C, Owns O WHERE C.vin = O.vin AND C.vin = '" + vin + "';";
		ResultSet vehicle = Utils.QueryHelper(query, Utils.stmt);
		if (!vehicle.next()) {
			System.out.println("ERROR selecting vehicle from database");
			return false;
		}

		System.out.println(vehicle.getString("login") + "\t" + vehicle.getString("model") + "\t"
				+ time.format(Utils.formatSQL).toString());

		// verify reservation Add
		System.out.print("Are you Sure you want to add this reservation (y/n)? ");

		String yesorno = null;
		while (yesorno == null) {
			yesorno = Utils.getInput();
			if (yesorno.equalsIgnoreCase("y")) {
				break;
			}
			if (yesorno.equalsIgnoreCase("n")) {
				System.out.println("Aborting Reservation");
				return false;
			}
			System.out.println("Please enter 'y' or 'n'");
			yesorno = null;
		}

		/* Attempt to insert reservation. If Exception is caught, return false */
		query = "SELECT MAX(resid) FROM Reservation;";
		ResultSet maxRes = Utils.QueryHelper(query, Utils.stmt);
		int resid;
		if (maxRes.next())
			resid = maxRes.getInt(1) + 1;
		else
			resid = 1;

		String addMe = "INSERT INTO Reservation VALUES ( ";
		addMe += resid + ", '" + Utils.currentUser + "', '" + vin + "', '" + time.format(Utils.formatSQL).toString()
				+ "');";

		try {
			Utils.UpdateHelper(addMe, Utils.stmt);
		} catch (Exception e) {
			System.out.println("Something went wrong while adding reservation");
			return false;
		}

		return true;
	}
}
