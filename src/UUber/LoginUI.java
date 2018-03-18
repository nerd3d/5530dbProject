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

		if(!Utils.SanitizeInput(u, "[a-zA-Z]{1}[a-zA-Z0-9]{3,19}")) {
			System.out.println("Input error. \nUsername needs to start with a letter.");
			System.out.println("Username length needs to be from 4 - 20 characters long");
			return;
		}
		
		if(!Utils.SanitizeInput(p, "[a-zA-Z0-9]{4,20}")) {
			System.out.println("Input error. \nPassword length needs to be from 4 - 20 characters long");
			return;
		}
		
		// attempt login
		String query = "SELECT login, name FROM User WHERE login = '" + u + "' AND password = '" + p + "';";
		ResultSet result = Utils.QueryHelper(query, StartPoint.connect.st);

		try {
			if (result.next()) {
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
		// asks for login info and user details (try to validate user name as soon as
		// entered)
		String u;
		String p;
		String name;
		String address;
		String phone;
		// asks for login info
		System.out.println("Please enter desired Login Name:");
		u = Utils.getInput();

		if (Utils.SanitizeInput(u, "[a-zA-Z]{1}[a-zA-Z0-9]{3,19}")) {
			ResultSet result = Utils.QueryHelper("SELECT * FROM User WHERE login = '" + u + "'; ", Utils.stmt);
			if (result.next()) {
				System.out.println("ERROR: Login Name already exists.\n");
				return;
			}

		} else {
			System.out.println("Input error. \nUsername needs to start with a letter.");
			System.out.println("Username length needs to be from 4 - 20 characters long, no spaces or special characters.");
			return;
		}

		// ask for password
		System.out.println("Please enter desired password:");
		p = Utils.getInput();
		
		if(!Utils.SanitizeInput(p, "[a-zA-Z0-9]{4,20}")) {
			System.out.println("Input error. \nPassword length needs to be from 4 - 20 characters long, no spaces, letters and numbers only.");
			return;
		}

		// ask for name
		System.out.println("Please enter your name:");
		name = Utils.getInput();
		// need to sanitize...
		if(!Utils.SanitizeInput(name, "[a-zA-Z ]{2,30}")) {
			System.out.println("Input error. \nName length needs to be from 2 - 30 characters long, letters only.");
			return;
		}
	
		// ask for address
		System.out.println("Please enter address or skip:");
		address = Utils.getInput();
		// need to sanitize...
		if(!Utils.SanitizeInput(address, "[a-zA-Z]{1}[a-zA-Z0-9 ]{3,256}")) {
			System.out.println("Input error. \nAddress may not include special characters.");
			return;
		}
		
		// ask for phone
		System.out.println("Please enter phone # or skip:");
		phone = Utils.getInput();
		// need to sanitize...
		if(!Utils.SanitizeInput(phone, "[0-9]{10}")) {
			System.out.println("Input error. \nPhone # needs to be 10 numbers, no spaces or dashes.");
			return;
		}

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
