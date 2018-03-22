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
				carResult = VehicleBrowserUI.ShowMenu("Reservation", time);
				if (carResult == null) {
					System.out.println("No valid Vehicle Chosen");
					return;
				}

				if (AddReservation(carResult, time)) {
					System.out.println("Reservation Successfully Added.");
				} else {
					System.out.println("Reservation Cancelled.");
				}

				return;
			}
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
			}
		} else {
			System.out.println("ERROR: expecting integer within range");
		}
	}

	/*
	 * Attempt to Add a reservation to the database. Return true if successful
	 */
	private static boolean AddReservation(String vin, LocalDateTime time) {
		// verify reservation Add
		System.out.println("Are you Sure you want to add this reservation (y/n)?");
		System.out.println("VIN: " + vin + "\nTime: " + time.format(Utils.formatSQL).toString());

		String result = null;
		while (result == null) {
			try {
				result = Utils.getInput();
			} catch (Exception e) {
				System.out.print("Please enter y or n: ");
			}
		}

		if (result.equalsIgnoreCase("n")) {
			System.out.println("Cancelling Reservation...");
			return false;
		}

		/* Attempt to insert reservation. If Exception is caught, return false */
		System.out.println("Not yet implemented");

		return true;
	}
}
