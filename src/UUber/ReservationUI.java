package UUber;

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
		String time;
		while (true) {
			// ask user for reservation time slot, or cancel
			System.out.println("*** Reserve a Car ***");
			System.out.println("1. Select Time");
			System.out.println("2. Cancel");
			switch (Utils.getInput()) {
			case "1":
				System.out.println("Enter a Reservation Time (example = 3-20 05 07)");
				time = Utils.getInput();
				// sanitize before breaking
				break;
			case "2":
				return;
			default:
				System.out.println("Invalid input.");
				continue;
			}

			// get a car from Vehicle Browser (with valid time slots)
			String vid = VehicleBrowserUI.ShowMenu(time);

			if (vid == null) {
				System.out.println("No vehicle available for given time.");
				continue; // search failed to produce vehicle, start local menu over
			}

			// Attempt to Add reservation to database
			if (AddReservation(vid, time)) {
				System.out.println("Car sucessfully reserved!");
				return;
			} else
				System.out.println("Unable to reserve vehicle for given time.");
		}
	}
	/*
	 * Generate, modify and display Reservation related queries
	 */
	private static void DisplayReservations() throws Exception {
		// get and display existing Reservations
		System.out.println("Here are your reservations...");
		System.out.println("blah...\nblah...\nblah...\nblah...");
		System.out.println("<press any key to go back>");
		// add input loop and whatever
		Utils.getInput();
	}

	/*
	 * Attempt to Add a reservation to the database.  Return true if successful
	 * */
	private static boolean AddReservation(String vid, String time) {
		// attempt to add reservation.  Throw exception if failed

		return false;
	}
}
