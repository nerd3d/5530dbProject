package UUber;

public class VehicleBrowserUI {

	public static String ShowMenu() throws Exception{
		while (true) {
			// welcome and list options...wait for input
			System.out.println("*** Vehicles ***");
			System.out.println("Show a bunch of cars...");
			System.out.println("need filter options etc...");
			System.out.println("<press any key to go back>");

			Utils.getInput();
			return null; // remove this when implemented!
		}
	}

	public static String ShowMenu(String time) throws Exception {
		while (true) {
			// welcome and list options...wait for input
			System.out.println("*** Vehicles available during "+ time +" ***");
			System.out.println("Show a bunch of cars...");
			System.out.println("need filter options etc...");
			System.out.println("<press any key to go back>");
			
			Utils.getInput();
			return null; // remove this when implemented!
		}
	}

}
