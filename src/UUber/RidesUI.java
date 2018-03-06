package UUber;

public class RidesUI {
	public static void ShowMenu() throws Exception{
		while (true) {
			// welcome and list options...wait for input
			System.out.println("*** Rides ***");
			System.out.println("Please make a selection:");
			System.out.println("1. Record a Ride");
			System.out.println("2. View Rides");
			System.out.println("3. Back");
			switch (Utils.getInput()) {
			case "1":
				// RecordRide();
				System.out.println("not hooked up yet");
				break;
			case "2":
				// DisplayRides();
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
