package UUber;

import java.sql.ResultSet;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

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
				Availability();
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
				System.out.println("Invalid menu selection");
				break;
			}
		}
	}

	private static void Availability() throws Exception {
		// Show user all current availability slots
		String query = "SELECT A.vin, model, year, day, time_from, time_to ";
		query += "FROM Available A, Owns O, Car C ";
		query += "WHERE A.vin = O.vin AND O.vin = C.vin ";
		query += "AND O.login = '" + Utils.currentUser;
		query += "' ORDER BY day, time_from;";

		while (true) {
			ResultSet avail = Utils.QueryHelper(query, Utils.stmt);
			System.out.println("*** Current Availability ***");
			int num = 1;
			while (avail.next()) {
				String av = num + "\t";
				av += avail.getString("model") + "\t";
				av += avail.getString("year") + "\t";
				DayOfWeek dow = DayOfWeek.of(Integer.parseInt(avail.getString("day")));
				av += dow.toString() + "\t";
				av += avail.getString("time_from") + "\t";
				av += avail.getString("time_to");
				System.out.println(av);
				num++;
			}

			// get input: Add availability, Modify availability, Remove Availability, Return
			System.out.println("*** Please make a selection ***");
			System.out.println("1. Add availability");
			System.out.println("2. Remove availability");
			System.out.println("3. Return");

			switch (Utils.getInput()) {
			case "1":
				if (AddAvailability()) {
					System.out.println("Availability Added");
				} else {
					System.out.println("Add aborted.");
				}
				break;
			case "2":
				// Get shift to delete
				System.out.print("Enter shift to remove: ");
				int deleteMe;
				try {
					deleteMe = Integer.parseInt(Utils.getInput());
				} catch (Exception e) {
					System.out.println("ERROR: Enter a number.");
					break;
				}
				// Get vin, day, time_from and time_to to be deleted
				avail.beforeFirst();
				avail.relative(deleteMe);

				// delete the entry from Available
				String delEntry = "DELETE FROM Available ";
				delEntry += "WHERE vin = '" + avail.getString("A.vin") + "' ";
				delEntry += "AND day = '" + avail.getString("day") + "' ";
				delEntry += "AND time_from = '" + avail.getString("time_from") + "' ";
				delEntry += "AND time_to = '" + avail.getString("time_to") + "'; ";
				Utils.UpdateHelper(delEntry, Utils.stmt);

				break;
			case "3":
				return;
			default:
				System.out.println("Invalid menu selection");
				break;
			}
		}
	}

	private static boolean AddAvailability() throws Exception {
		String dayStr;
		List<String> vins, allVins;
		DayOfWeek day;
		int from, to, duration;

		while (true) {
			// get day for shift start
			System.out.print("Please enter a Day (SU, MO, TU, WE, TH, FR, SA): ");
			dayStr = Utils.getInputToUpper();
			switch (dayStr) {
			case "SU":
				day = DayOfWeek.SUNDAY;
				break;
			case "MO":
				day = DayOfWeek.MONDAY;
				break;
			case "TU":
				day = DayOfWeek.TUESDAY;
				break;
			case "WE":
				day = DayOfWeek.WEDNESDAY;
				break;
			case "TH":
				day = DayOfWeek.THURSDAY;
				break;
			case "FR":
				day = DayOfWeek.FRIDAY;
				break;
			case "SA":
				day = DayOfWeek.SATURDAY;
				break;
			default:
				System.out.println("Invalid Entry or Format");
				return false;
			}
			// get time for shift start
			System.out.print("Please enter the time you'd like to begin your shift (0 - 23):");
			try {
				from = Integer.parseInt(Utils.getInput());
				if (from < 0 || from > 23)
					throw new Exception("Invalid Entry or Format");
			} catch (Exception e) {
				System.out.println("Invalid Entry or Format");
				return false;
			}
			// get duration of shift
			System.out.print("Please enter the duration of your shift (max 12):");
			try {
				duration = Integer.parseInt(Utils.getInput());
				if (duration < 1 || duration > 12)
					throw new Exception("Invalid Entry or Format");
			} catch (Exception e) {
				System.out.println("Invalid Entry or Format");
				return false;
			}

			// show owned cars
			ResultSet ownedCars = Utils
					.QueryHelper("SELECT Car.vin, model, year FROM Owns, Car WHERE Owns.vin = Car.vin AND login = '"
							+ Utils.currentUser + "';", Utils.stmt);
			int row = 1;
			vins = new ArrayList<String>();
			allVins = new ArrayList<String>();
			while (ownedCars.next()) {
				allVins.add(ownedCars.getString("vin"));
				System.out.println(row + "\t" + ownedCars.getString("vin") + "\t" + ownedCars.getString("model") + "\t"
						+ ownedCars.getString("year"));

				row++;
			}
			// get which cars to include for shift
			System.out.print("Select Car(s) available during this shift (separate with spaces OR 'a' for all):");
			String cars = Utils.getInputToLower();
			if (cars.equalsIgnoreCase("a")) {
				vins = allVins;
			} else {
				// determine which cars to remove
				String[] carList = cars.split("\b");

				try {
					for (String num : carList) {
						System.out.print(num + " ");
						int indx = Integer.parseInt(num) - 1;
						vins.add(allVins.get(indx));
					}
				} catch (Exception e) {
					System.out.println("Invalid Entry or Format");
					return false;
				}
			}

			// attempt to insert shift for given cars
			for (String vin : vins) {
				to = from + duration;
				if (to > 24) {
					if (!InsertAvailability(vin, day, from, 24))
						return false;
					if (!InsertAvailability(vin, day.plus(1), 0, to - 24)) {
						// clean up last insert
						return false;
					}
				} else {
					if (!InsertAvailability(vin, day, from, to))
						return false;
				}
			}
			return true;
		}

	}

	private static boolean InsertAvailability(String vin, DayOfWeek day, int from, int to) throws Exception {
		String conflicts = "SELECT time_from, time_to FROM Available ";
		conflicts += "WHERE vin = '" + vin + "' ";
		conflicts += "AND day = '" + day.getValue() + "';";
		ResultSet conRes = Utils.QueryHelper(conflicts, Utils.stmt);
		while (conRes.next()) {
			// validate From-To's don't overlap
			int exFrom = conRes.getInt("time_from");
			int exTo = conRes.getInt("time_to");
			if (exFrom < from && from < exTo) {
				System.out.println("ERROR: Availability overlaps with existing time block for this car");
				return false;
			}
			if (exFrom < to && to < exTo) {
				System.out.println("ERROR: Availability overlaps with existing time block for this car");
				return false;
			}
		}

		String insert = "INSERT INTO Available VALUES ('" + vin + "','" + day.getValue() + "','" + from + "','" + to
				+ "');";
		if (Utils.UpdateHelper(insert, Utils.stmt) < 0)
			return false;

		return true;
	}

	private static boolean ModifyCar() throws Exception {
		// Show user all owned cars
		String query = "SELECT O.vin, make, model, year, category ";
		query += "FROM Owns O, Car C ";
		query += "WHERE O.vin = C.vin ";
		query += "AND login = '" + Utils.currentUser + "';";
		ResultSet owned = Utils.QueryHelper(query, Utils.stmt);

		int num = 1;
		while (owned.next()) {
			System.out.print(num + "\t");
			System.out.print(owned.getString("O.vin") + "\t");
			System.out.print(owned.getString("make") + "\t");
			System.out.print(owned.getString("model") + "\t");
			System.out.print(owned.getString("year") + "\t");
			System.out.print(owned.getString("category") + "\n");
			num++;
		}

		// get input for which car to edit
		System.out.print("Select a Vehicle to Modify: ");
		num = Integer.parseInt(Utils.getInput());
		owned.beforeFirst();
		owned.relative(num);
		String vinSelected = owned.getString("O.vin");

		// get input for which field to edit / or delete car?
		System.out.println("Select a field to Edit: ");
		System.out.println("VIN\nMake\nModel\nYear\nCategory");
		String field = Utils.getInputToLower();
		String changeTo;
		String swapEm = "";

		switch (field) {
		case "vin":
			// get new value for field
			System.out.print("Enter the new VIN: ");
			changeTo = Utils.getInput();

			if (Utils.SanitizeInput(changeTo, "[a-zA-Z0-9]{17}")) {
				ResultSet result = Utils.QueryHelper("SELECT * FROM Car WHERE vin = '" + changeTo + "'; ", Utils.stmt);
				if (result.next()) {
					System.out.println("ERROR: Vehicle already exists in records.\n");
					return false;
				}
			} else {
				System.out.println("Input error. \nVIN needs to be exactly 17 characters long.");
				System.out.println("Only letters and numbers are vild characters");
				return false;
			}

			// update to new value
			swapEm = "UPDATE Car SET vin = '" + changeTo + "' ";

			break;
		case "make":
			// get new value for field
			System.out.print("Enter Make of Car: ");
			changeTo = Utils.getInput();

			if (!Utils.SanitizeInput(changeTo, "[a-zA-Z ]{3,10}")) {
				System.out.println("Input error. \nMake length needs to be from 3 - 10 characters long");
				return false;
			}

			// update to new value
			swapEm = "UPDATE Car SET make = '" + changeTo + "' ";

			break;
		case "model":
			// get new value for field
			System.out.print("Enter Model of Car: ");
			changeTo = Utils.getInput();

			if (!Utils.SanitizeInput(changeTo, "[a-zA-Z ]{3,10}")) {
				System.out.println("Input error. \nMake length needs to be from 3 - 10 characters long");
				return false;
			}

			// update to new value
			swapEm = "UPDATE Car SET model = '" + changeTo + "' ";

			break;
		case "year":
			// get new value for field
			System.out.print("Enter Year of Car: ");
			changeTo = Utils.getInput();

			if (!Utils.SanitizeInput(changeTo, "[0-9]{4}")) {
				System.out.println("Input error. \nYear needs to be 4 digits");
				return false;
			}

			// update to new value
			swapEm = "UPDATE Car SET year = '" + changeTo + "' ";

			break;
		case "category":
			// select a category
			changeTo = GetCategory(true);

			// update to new value
			swapEm = "UPDATE Car SET category = '" + changeTo + "' ";

			break;
		default:
			System.out.println("ERROR: No such Field.");
			return false;
		}

		// edit entry
		swapEm += "WHERE vin = '" + vinSelected + "';";

		if (Utils.UpdateHelper(swapEm, Utils.stmt) >= 0)
			return true;

		return false;
	}

	public static String GetCategory(Boolean driver) throws Exception {
		// select a category
		ResultSet cats = Utils.QueryHelper("SELECT * FROM Category;", Utils.stmt);
		boolean valid = false;

		while (!valid) {
			if(driver)
				System.out.println("Please select an appropriate category for your car:");
			else
				System.out.println("Please select an appropriate category for your car, or skip (enter)");

			while (cats.next()) {
				System.out.println(cats.getString("category"));
			}

			String select = Utils.getInputToLower();
			if(!driver && select.equals("")) {return select;}
			if (Utils.SanitizeInput(select, "[a-zA-Z0-9]{1,20}")) {
				cats.beforeFirst();
				while (cats.next()) {
					String cat = cats.getString("category");
					if (cat.equalsIgnoreCase(select)) {
						valid = true;
						return select;
					}
				}
			}
		}

		throw new Exception("Invalid category selected");
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
		category = GetCategory(true);

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
