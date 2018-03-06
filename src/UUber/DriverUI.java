package UUber;

public class DriverUI {
	public static void ShowMenu() throws Exception{
		while (true) {
			// welcome and list options...wait for input
			System.out.println("*** Driver Options ***");
			System.out.println("Please make a selection:");
			System.out.println("1. Adjust Availability");
			System.out.println("2. Register a Car");
			System.out.println("3. Back");
			switch (Utils.getInput()) {
			case "1":
				// Availability();
				System.out.println("not hooked up yet");
				break;
			case "2":
				// RegisterCar();
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
