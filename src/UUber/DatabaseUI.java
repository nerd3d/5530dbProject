package UUber;

public class DatabaseUI {
	public static void ShowMenu() throws Exception{
		while (true) {
			// welcome and list options...wait for input
			System.out.println("*** UUber Main Menu ***");
			System.out.println("Please make a selection:");
			System.out.println("1. Reservation");
			System.out.println("2. Rides");
			System.out.println("3. Vehicles");
			System.out.println("4. Users");
			System.out.println("5. Drive");
			System.out.println("6. Quit");

			switch (Utils.getInput()) {
			case "1":
				ReservationUI.ShowMenu();
				//System.out.println("not hooked up yet");
				break;
			case "2":
				RidesUI.ShowMenu();
				//System.out.println("not hooked up yet");
				break;
			case "3":
				VehicleBrowserUI.ShowMenu();
				//System.out.println("not hooked up yet");
				break;
			case "4":
				UserUI.ShowMenu();
				//System.out.println("not hooked up yet");
				break;
			case "5":
				DriverUI.ShowMenu();
				//System.out.println("not hooked up yet");
				break;
			case "6":
				if(Quit())
					return;
				else
					break;
			default:
				System.out.println("Invalid input.");
				break;
			}
		}
	}
	private static boolean Quit() throws Exception{
		// you sure? (y/n) y -> return;
		// n -> showmenu.
		System.out.println("Are you sure you want to quit? (y/n)");
		switch(Utils.getInput().toLowerCase()) {
		case "y":
			System.out.println("Goodbye! Thank you for using UUber!");
			return true;
		case "n":
			return false;
		default:
			System.out.println("Invalid input. Please type 'y' or 'n'.");
		}
		return false;
	}
}
