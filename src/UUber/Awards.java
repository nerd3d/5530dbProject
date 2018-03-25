package UUber;

import java.sql.ResultSet;

public class Awards {
	public static void ShowMenu() throws Exception {
		while (true) {
			// welcome and list options...wait for input
			System.out.println("*** Top Users ***");
			System.out.println("Please make a selection:");
			System.out.println("1. View most trusted users.");
			System.out.println("2. View most useful users.");
			System.out.println("3. Back");
			switch (Utils.getInput()) {
			case "1":
				topTrusted();
				break;
			case "2":
				topUseful();
				break;
			case "3":
				return;
			default:
				System.out.println("Invalid input.");
				break;
			}
		}
	}
	private static void topTrusted() {
		int num = getNumToShow();
		if(num == -1)
			return;
		try {
			String queryTrusted = "SELECT login2 AS user, SUM(IF(trusted = 1, 1, -1)) AS trust_score FROM Trust GROUP BY login2 ORDER BY trust_score desc LIMIT "+num+";";
			ResultSet r = Utils.QueryHelper(queryTrusted, Utils.stmt);
			System.out.println("User\tTrust_Score");
			System.out.println("____________________");
			while(r.next())
			{
				System.out.print(r.getString("user") + "\t");
				System.out.print(r.getString("trust_score") + "\n");
			}
		}catch(Exception e) { System.out.println("Failed to query.");}
	}
	private static void topUseful() {
		int num = getNumToShow();
		if(num == -1)
			return;
		
	}
	private static int getNumToShow() {
		int num;
		System.out.println("How many top users do you wish to display?");
		try 
		{
			num = Integer.parseInt(Utils.getInput());
			if(num < 0)
				throw new NumberFormatException();
			return num;
		}
		catch(Exception e) {System.out.println("Must give a number (e.g. 5). Positive numbers only.");}
		return -1;
	}
}
