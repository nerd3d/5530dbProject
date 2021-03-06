package UUber;

import java.sql.*;

public class UserUI {
	public static void ShowMenu() throws Exception {
		while (true) {
			// welcome and list options...wait for input
			System.out.println("*** User Relations ***");
			System.out.println("Please make a selection:");
			//System.out.println("1. Browse Users");
			System.out.println("1. Trusted Users");
			System.out.println("2. Mistrusted Users");
			System.out.println("3. Back");
			switch (Utils.getInput()) {
			//case "1":
			//	UserBrowserUI.ShowMenu();
			//	break;
			case "1":
				DisplayTrusted(true);
				break;
			case "2":
				DisplayTrusted(false);
				break;
			case "3":
				return;
			default:
				System.out.println("Invalid input.");
				return;
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
			query += "FROM Trust ";
			query += "WHERE trusted = TRUE ";
			query += "AND login = '" + Utils.currentUser + "'" ;
			query += ";";
		} else {
			System.out.println("*** Mistrusted Users ***");
			query = "SELECT login2 ";
			query += "FROM Trust ";
			query += "WHERE trusted = FALSE ";
			query += "AND login = '" + Utils.currentUser + "'" ;
			query += ";";
		}

		ResultSet result = Utils.QueryHelper(query, StartPoint.connect.st);
		while (result.next()){
			System.out.println(result.getString(1));	
		}
		//options:
		System.out.println("******");
		if(trusted)
			System.out.println("1. Add trusted User");
		else
			System.out.println("1. Add mistrusted User");
		System.out.println("2. Back");
		
		switch (Utils.getInput()) {
		//case "1":
		//	UserBrowserUI.ShowMenu();
		//	break;
		case "1":
			if(trusted)
				System.out.println("Please provide trusted username.");
			else
				System.out.println("Please provide mistrusted username.");
			String usrName = Utils.getInput();
			if(usrName.equals(Utils.currentUser)) {
				System.out.println("Cannot put your own name on trust list.");
				return;
			}
			// need to sanitize...
			if(!Utils.SanitizeInput(usrName, "[a-zA-Z]{1}[a-zA-Z0-9]{3,19}")) {
				System.out.println("Input error. \nUsername needs to start with a letter.");
				System.out.println("Username length needs to be from 4 - 20 characters long, no spaces or special characters.");
				return;
			}
			//attempt to insert new trusted/mistrusted user to trust table
			//INSERT INTO table (id, name, age) VALUES(1, "A", 19) ON DUPLICATE KEY UPDATE name="A", age=19
			int r = Utils.UpdateHelper("INSERT INTO Trust (login, login2, trusted) VALUES('"+ Utils.currentUser + 
					"', '"+ usrName + "'," + trusted +") ON DUPLICATE KEY UPDATE trusted = " + trusted +";", Utils.stmt);
			if(-1 == r) 
			{
				System.out.println("User does not exist or connection failed.");
			}
			else
			{
				System.out.println("Trust list updated.");
			}
			return;
		case "2":
			return;
		default:
			System.out.println("Invalid input.");
			return;
		}
	}
}
