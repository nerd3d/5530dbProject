package UUber;

public class UserUI {
	public static void ShowMenu() throws Exception{
		while (true) {
			// welcome and list options...wait for input
			System.out.println("*** User Relations ***");
			System.out.println("Please make a selection:");
			System.out.println("1. Browse Users");
			System.out.println("2. Trusted Users");
			System.out.println("3. Mistrusted Users");
			System.out.println("4. Back");
			switch (Utils.getInput()) {
			case "1":
				UserBrowserUI.ShowMenu();
				break;
			case "2":
				DisplayTrusted(true);
				break;
			case "3":
				DisplayTrusted(false);
				break;
			case "4":
				return;
			default:
				System.out.println("Invalid input.");
				break;
			}
		}
	}

	/*
	 * Query users' trusted users true = trusted / false = mistrusted
	 * */
	private static void DisplayTrusted(boolean trusted) throws Exception {
		// TODO Auto-generated method stub

		if(trusted)
			System.out.println("Here are all of your trusted users\nblah...\nblah...");
		else
			System.out.println("Here are all of your mistrusted users\nblah...\nblah...");
		System.out.println("<press any key to go back>");
		Utils.getInput();
	}
}
