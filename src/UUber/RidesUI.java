package UUber;

public class RidesUI {
	public static void ShowMenu() throws Exception{
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
	 * */
	private static void RecordRide() throws Exception {
		// TODO Auto-generated method stub

		System.out.println("User will enter ride details here...");
		Utils.getInput();
	}
	
	/*
	 * Query for rides related to user
	 * */
	private static void DisplayRides() throws Exception {
		// TODO Auto-generated method stub

		System.out.println("Show history of rides...");
		System.out.println("<press any key to go back>");
		Utils.getInput();
	}

}
