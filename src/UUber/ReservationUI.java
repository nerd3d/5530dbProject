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
				String vid = VehicleBrowserUI.ShowMenu();
				System.out.println("not hooked up yet");
				break;
			case "2":
				// DisplayReservations();
				System.out.println("not hooked up yet");
				break;
			case "3":
				return;
			default:
				System.out.println("Invalid input.");
				break;
			}
		}
	}
}
