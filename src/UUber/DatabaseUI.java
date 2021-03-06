package UUber;

import java.sql.ResultSet;
import java.time.LocalDateTime;

public class DatabaseUI {
	public static boolean isDriver = false;

	public static void ShowMenu() throws Exception {
		// determine and set the isDriver boolean
		ResultSet result = Utils.QueryHelper("SELECT * FROM Driver WHERE login = '" + Utils.currentUser + "'",
				StartPoint.connect.st);
		isDriver = result.next();

		while (true) {
			// welcome and list options...wait for input
			System.out.println("*** UUber Main Menu ***");
			System.out.println("Please make a selection:");
			System.out.println("1. Reservations");
			System.out.println("2. Rides");
			System.out.println("3. Vehicles");
			System.out.println("4. Users");
			System.out.println("5. UUber Statistics");
			System.out.println("6. Top Users");
			if (isDriver) {
				System.out.println("7. Driver Options");
				System.out.println("8. Logout");
			} else {
				System.out.println("7. Logout");
			}

			switch (Utils.getInput()) {
			case "1":
				ReservationUI.ShowMenu();
				break;
			case "2":
				RidesUI.ShowMenu();
				break;
			case "3":
				VehicleBrowserUI.ShowMenu("MainMenu", LocalDateTime.parse(LocalDateTime.now().format(Utils.formatINP).toString(), Utils.formatINP));
				break;
			case "4":
				UserUI.ShowMenu();
				break;
			case "5":
				StatisticsUI.ShowMenu();
				break;
			case "6":
				Awards.ShowMenu();
				break;
			case "7":
				if (isDriver) {
					DriverUI.ShowMenu();
					break;
				}
			case "8":
				if (Quit())
					return;
				else
					break;
			default:
				System.out.println("Invalid input.");
				break;
			}
		}
	}

	private static boolean Quit() throws Exception {
		System.out.println("Logout? (y/n)");
		switch (Utils.getInput().toLowerCase()) {
		case "y":
			System.out.println("Logging Out..");
			return true;
		case "n":
			return false;
		default:
			System.out.println("Invalid input. Please type 'y' or 'n'.");
		}
		return false;
	}
}
