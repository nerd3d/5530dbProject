package UUber;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.ResultSet;

public class LoginUI {

	public static void ShowMenu() throws Exception {
		while (true) {
			// welcome and list options...wait for input
			System.out.println("Welcome to UUber database!");
			System.out.println("Please make a selection:");
			System.out.println("1. Login");
			System.out.println("2. Create Account");
			System.out.println("3. Quit");

			switch (Utils.getInput()) {
			case "1":
				Login();
				break;
			case "2":
				CreateAccount();
				break;
			case "3":
				return;
			default:
				System.out.println("Invalid input.");
				break;
			}
		}
	}

	private static void Login() throws Exception {
		String u;
		String p;
		// asks for login info
		System.out.println("Please enter User Login:");
		u = Utils.getInput();
		System.out.println("Please enter Password:");
		p = Utils.getInput();

		try {
		u = Utils.SanitizeInput(u, "[a-zA-Z]{1}[a-zA-Z0-9]{3,19}"); // username starts with a letter and is followed by 3 or more characters (number and letter only)
		p = Utils.SanitizeInput(p, "[a-zA-Z0-9]{4,20}"); // password needs to be 4 or more characters long (number and letter only)
		} catch (Exception e) {
			System.out.println("Input error. \nUsername needs to start with a letter."); 
			System.out.println("Username and Password lengths need to be from 4 - 20 characters long");
			return;
		}

		// attempt login
		String query = "SELECT login, name FROM User WHERE login = '" + u + "' AND password = '" + p + "';";
		ResultSet result = Utils.QueryHelper(query, StartPoint.connect.st);

		try {
			if(result.next()) {
				// if login successful: call MainMenu.showmenu
				System.out.println("Login successful. Welcome, " + result.getString("name"));
				Utils.currentUser = u;
				DatabaseUI.ShowMenu();
			}
			// else: return to showMenu with failure message.
			else
				System.out.println("Invalid user name.");
		} catch (Exception e) {
			System.out.println("Unreadable query result.");
		}
	}

	private static void CreateAccount() throws Exception {
		// asks for login info and user details (try to validate user name as soon as entered)
		String u;
		String p;
		String name;
		String address;
		String phone;
		// asks for login info
		System.out.println("Please enter desired Login Name:");
		u = Utils.getInput();

		try {
			u = Utils.SanitizeInput(u, "[a-zA-Z]{1}[a-zA-Z0-9]{3,19}"); // username starts with a letter and is followed by 3 or more characters (number and letter only)
			ResultSet result = Utils.QueryHelper("SELECT * FROM User WHERE login = '"+u+"'; ", Utils.stmt);
			if(result.next()) {
				System.out.println("ERROR: Login Name already exists.\n");
				return;
			}
			} catch (Exception e) {
				System.out.println("Input error. \nUsername needs to start with a letter."); 
				System.out.println("Username lengths need to be from 4 - 20 characters long");
				return;
			}

		System.out.println("Please enter desired password:");
		p = Utils.getInput();
		System.out.println("Please enter your name:");
		name = Utils.getInput();
		System.out.println("Please enter address or skip:");
		address = Utils.getInput();
		System.out.println("Please enter phone # or skip:");
		phone = Utils.getInput();
		// sanitizeInput(variable number of inputs) <- to be implemented as own static
		// class for all menus.
		// attempt to create new user
		String query;
		if (address == "" && phone == "") {
			query = "INSERT INTO User VALUES ('" + u + "','" + p + "','" + name + "',NULL,NULL)" + ";";
		} else if (address == "")
			query = "INSERT INTO User VALUES ('" + u + "','" + p + "','" + name + "',NULL,'" + phone + "')" + ";";
		else if (phone == "")
			query = "INSERT INTO User VALUES ('" + u + "','" + p + "','" + name + "','" + address + "',NULL)" + ";";
		else
			query = "INSERT INTO User VALUES ('" + u + "','" + p + "','" + name + "','" + address + "','" + phone + "')"
					+ ";";

		int result = Utils.UpdateHelper(query, StartPoint.connect.st);
		switch (result) {
		case 0:
			System.out.println("Failed to create user.");
			break;
		case 1:
			System.out.println("Account creation successful! Welcome, " + name);
			Utils.currentUser = u;
			DatabaseUI.ShowMenu();
			break;
		default:
			System.out.println("Error occured.");
			break;
		}
	}
}
