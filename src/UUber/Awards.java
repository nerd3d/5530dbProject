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
			r.close();
		}catch(Exception e) { System.out.println("Failed to query.");}
	}
	private static void topUseful() {
		int num = getNumToShow();
		if(num == -1)
			return;
		try {
			//Usefulness references fids, but also contains scores
			//SELECT F.login, AVG(U.score) AS useful_score FROM Feedback F JOIN Usefulness U ON F.fid = U.fid GROUP BY login ORDER BY useful_score desc LIMIT 2;
			String queryUseful = "SELECT F.login AS user, AVG(U.score) AS useful_score FROM Feedback F JOIN Usefulness U ON F.fid = U.fid GROUP BY F.login ORDER BY useful_score desc LIMIT "+num+";";
			ResultSet rs = Utils.QueryHelper(queryUseful, Utils.stmt);
			System.out.println("User\tUseful_Score");
			System.out.println("____________________");
			while(rs.next())
			{
				System.out.print(rs.getString("user") + "\t");
				System.out.print(rs.getString("useful_score") + "\n");
			}
			rs.close();
		}catch(Exception e) { System.out.println("Failed to query.");}
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
