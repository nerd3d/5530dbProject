package UUber;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LoginUI {

	public static void ShowMenu() throws Exception{
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

	private static void Login() throws Exception{
		String u;
		String p;
		// asks for login info
		System.out.println("Please enter User Login:");
		u = Utils.getInput();
		System.out.println("Please enter password:");
		p = Utils.getInput();
		
		//sanitizeInput(variable number of inputs)  <- to be implemented as own static class for all menus.
		
		// attempt login
		
		// if login successful: call MainMenu.showmenu
		if(u.equals("master") && p.equals("1234"))
		{
			System.out.println("Login successful.");
			Utils.currentUser = u;
			DatabaseUI.ShowMenu();
		}
		// else: return to showMenu with failure message.
		else
			System.out.println("Invalid user name.");
		
	}

	private static void CreateAccount() throws Exception{
		// asks for login info and user details (try to validate user name as soon as
		// entered)
		String u;
		String p;
		// asks for login info
		System.out.println("Please enter desired Login Name:");
		u = Utils.getInput();
		System.out.println("Please enter desired password:");
		p = Utils.getInput();
		
		//sanitizeInput(variable number of inputs)  <- to be implemented as own static class for all menus.
		
		// attempt login
		
		// if login created, return to login screen
		if(u.equals("master") && p.equals("1234"))
		{
			System.out.println("User "+ u +" created.");
			return;//to login menu
		}
		// else: return to login screen with failure message.
		else
			System.out.println("Invalid user name or password.");
		// attempt to create new user
		// if successful: return to show menu, success message
		// else: return to showMenu with failure message.
	}

	private static boolean Quit() throws Exception{
		// you sure? (y/n) y -> return;
		// n -> showmenu.
		System.out.println("Are you sure you want to quit? (y/n)");
		switch(Utils.getInput().toLowerCase()) {
		case "y":
			System.out.println("Goodbye! Thank you for using UUber!");
			Utils.currentUser = "";
			return true;
		case "n":
			return false;
		default:
			System.out.println("Invalid input. Please type 'y' or 'n'.");
		}
		return false;
	}
}
