package UUber;

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
			}

			if (time != null) {
				carResult = VehicleBrowserUI.ShowMenu(time);
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

		query += "SELECT vin, time ";
		query += "FROM Reservation ";
		query += "WHERE time > NOW() ";
		query += "AND login = '" + Utils.currentUser + "'";
		query += ";";

		System.out.println("*** Future Reservations ***");
		System.out.println(query);
		System.out.println("<press any key to go back>");
		// add input loop and whatever
		Utils.getInput();
	}

	/*
	 * Attempt to Add a reservation to the database. Return true if successful
	 */
	private static boolean AddReservation(String vin, LocalDateTime time) {
		// attempt to add reservation. return false if fails.
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
		
		/* Attempt to insert reservation. If Exception is caught, return false*/
		System.out.println("Not yet implemented");

		return true;
	}
}
