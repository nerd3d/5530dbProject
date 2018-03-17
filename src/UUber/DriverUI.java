package UUber;

import java.sql.ResultSet;

public class DriverUI {
	public static void ShowMenu() throws Exception {
		while (true) {
			// welcome and list options...wait for input
			System.out.println("*** Driver Options ***");
			System.out.println("Please make a selection:");
			System.out.println("1. Adjust Availability");
			System.out.println("2. Modify Car Information");
			System.out.println("3. Register a Car");
			System.out.println("4. Back");
			switch (Utils.getInput()) {
			case "1":
				// Availability();
				System.out.println("not hooked up yet");
				break;
			case "2":
				if (ModifyCar()) {
					System.out.println("Car successfuly modified");
				} else {
					System.out.println("Car modification Canceled.");
				}
				break;
			case "3":
				if (RegisterCar()) {
					System.out.println("Car added to account");
				} else {
					System.out.println("Car add Canceled.");
				}
				break;
			case "4":
				return;
			default:
				System.out.println("Invalid input.");
				break;
			}
		}
	}

	private static boolean ModifyCar() {

		return true;
	}

	private static boolean RegisterCar() throws Exception {
		String vin, category = null, make, model, year;

		// asks for vehicle identification number
		System.out.println("Please enter Car VIN: ");
		vin = Utils.getInput();

		if (Utils.SanitizeInput(vin, "[a-zA-Z0-9]{17}")) {
			ResultSet result = Utils.QueryHelper("SELECT * FROM Car WHERE vin = '" + vin + "'; ", Utils.stmt);
			if (result.next()) {
				System.out.println("ERROR: Vehicle already exists in records.\n");
				return false;
			}
		} else {
			System.out.println("Input error. \nVIN needs to be exactly 17 characters long.");
			System.out.println("Only letters and numbers are vild characters");
			return false;
		}

		// ask for make of vehicle
		System.out.println("Please enter Make of Car: ");
		make = Utils.getInput();

		if (!Utils.SanitizeInput(make, "[a-zA-Z ]{3,10}")) {
			System.out.println("Input error. \nMake length needs to be from 3 - 10 characters long");
			return false;
		}

		// ask for model of vehicle
		System.out.println("Please enter Model of car: ");
		model = Utils.getInput();

		if (!Utils.SanitizeInput(model, "[a-zA-Z0-9 ]{1,16}")) {
			System.out.println("Input error. \nModel length needs to be from 1 - 16 characters long");
			return false;
		}

		// ask for year of vehicle
		System.out.println("Please enter Year of production: ");
		year = Utils.getInput();

		if (!Utils.SanitizeInput(year, "[0-9]{4}")) {
			System.out.println("Input error. \nYear needs to be 4 digits");
			return false;
		}

		// select a category
		ResultSet cats = Utils.QueryHelper("SELECT * FROM Category", Utils.stmt);
		boolean valid = false;

		while (!valid) {
			System.out.println("Please select an appropriate category for your car:");

			while (cats.next()) {
				System.out.println(cats.getString("category"));
			}

			String select = Utils.getInputToLower();
			if (Utils.SanitizeInput(select, "[a-zA-Z0-9]{1,20}")) {
				while (cats.previous()) {
					if (cats.getString("category").equalsIgnoreCase(select)) {
						valid = true;
						category = select;
					}
				}
			}
		}

		// All info gathered. Display and confirm before submitting
		while (true) {
			System.out.println("\nPlease review info for correctness before adding:");
			System.out.println("Category: " + category + " / VIN: " + vin + "/  Make: " + make + " / Model: " + model
					+ " / Year: " + year);
			System.out.print("Okay to add (y / n): ");
			switch (Utils.getInput()) {
			case "y":
				// SUCCESS! Add the car!
				String addStr = "INSERT INTO Car ";
				addStr += "VALUES ('" + vin + "', ";
				addStr += "'" + category + "', ";
				addStr += "'" + make + "', ";
				addStr += "'" + model + "', ";
				addStr += year + "); ";

				if (Utils.UpdateHelper(addStr, Utils.stmt) < 0) {
					System.out.println("ERROR: something went wrong when adding car.");
					return false;
				}

				// Make sure to add it to Owns table too
				String ownStr = "INSERT INTO Owns ";
				ownStr += "VALUES ('" + Utils.currentUser + "', ";
				ownStr += "'" + vin + "');";

				if (Utils.UpdateHelper(ownStr, Utils.stmt) < 0) {
					System.out.println("ERROR: something went wrong when adding car.");

					// remove vehicle from Car list
					Utils.UpdateHelper("DELETE FROM Car WHERE vin = '" + vin + "';", Utils.stmt);

					return false;
				}

				return true;
			case "n":
				return false;
			}
		}
	}
}
