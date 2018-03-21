package UUber;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.util.Arrays;

public class LoginUI {
	//grabbed an array of states of the web to restrict input to real states.
static String[] states = {"california,", "alabama,", "arkansas,", "arizona,", "alaska,", "colorado,", "connecticut,", "delaware,", "florida,", "georgia,", "hawaii,", "idaho,", "illinois,", "indiana,", "iowa,", "kansas,", "kentucky,", "louisiana,", "maine,", "maryland,", "massachusetts,", "michigan,", "minnesota,", "mississippi,", "missouri,", "montana,", "nebraska,", "nevada,", "new Hampshire,", "new jersey,", "new mexico,", "new york,", "north carolina,", "north dakota,", "ohio,", "oklahoma,", "oregon,", "pennsylvania,", "rhode Island,", "south carolina,", "south dakota,", "tennessee,", "texas,", "utah,", "vermont,", "virginia,", "washington,", "west virginia,", "wisconsin,", "wyoming" };

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
			}
			// else: return to showMenu with failure message.
			else
				System.out.println("Invalid user name.");
		} catch (Exception e) {
			System.out.println("Unreadable query result.");
		}
		if(Utils.currentUser.equals(u)) {
			DatabaseUI.ShowMenu();
		}
	}

	private static void CreateAccount() throws Exception {
		// asks for login info and user details (try to validate user name as soon as
		// entered)
		String u;
		String p;
		String name;
		String state;
		String city;
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
		System.out.println("Please enter state:");
		state = Utils.getInputToLower();
		// need to sanitize...
		if(state != null && !state.equals("") && Arrays.asList(LoginUI.states).contains(state))
		{
			if(!Utils.SanitizeInput(state, "[a-zA-Z ]{4,14}")) {
				System.out.println("State needs to be from 4 - 14 characters long, Letters and spaces only.");
				return;
			}
		}
		else
		{
			System.out.println("invalid input");
			return;
		}
		// ask for address
		System.out.println("Please enter city:");
		city = Utils.getInputToLower();
		// need to sanitize...
		if(city != null && !city.equals(""))
		{
			if(!Utils.SanitizeInput(city, "[a-zA-Z ]{1,30}")) {
				System.out.println("City needs to be from 1 - 30 characters long, Letters and spaces only.");
				return;
			}
		}
		else
		{
			System.out.println("invalid input");
			return;
		}
		
		
		// ask for phone
		System.out.println("Please enter phone # or skip:");
		phone = Utils.getInput();
		// need to sanitize...
		if(phone != null && !phone.equals("")) {
			if(!Utils.SanitizeInput(phone, "[0-9]{10}")) {
				System.out.println("Input error. \nPhone # needs to be 10 numbers, no spaces or dashes.");
				return;
			}	
		}

		// attempt to create new user
		String query;
		if (phone.equals(""))
			query = "INSERT INTO User VALUES ('" + u + "','" + p + "','" + name + "', NULL,'" + state + "','" + city + "')" + ";";
		else
			query = "INSERT INTO User VALUES ('" + u + "','" + p + "','" + name + "','"+ phone + "','" + state + "','" + city + "')" + ";";

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
