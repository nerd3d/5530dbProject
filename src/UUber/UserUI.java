package UUber;

public class UserUI {
	public static void ShowMenu() throws Exception {
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
	 */
	private static void DisplayTrusted(boolean trusted) throws Exception {
		String query = "";

		if (trusted) {
			System.out.println("*** Trusted Users ***");
			query = "SELECT login2 ";
			query += "FROM Trust";
			query += "WHERE trusted = TRUE ";
			query += "AND login = '" + StartPoint.currentUser + "'" ;
			query += ";";
		} else {
			System.out.println("*** Mistrusted Users ***");
			query = "SELECT login2 ";
			query += "FROM Trust";
			query += "WHERE trusted = FALSE ";
			query += "AND login = '" + StartPoint.currentUser + "'" ;
			query += ";";
		}

		System.out.println(query); // this will need to query, then parse results for output 
		System.out.println("<press any key to go back>");
		Utils.getInput();
	}
}
