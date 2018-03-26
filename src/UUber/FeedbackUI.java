package UUber;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FeedbackUI {
	static Boolean end = false;

	public static void viewFeedback(String vin) {
		end = false;
		while (!end) {
			// get all feedbacks for this vin
			String query = "SELECT login, date, rating, text, fid FROM Feedback WHERE vin = '" + vin + "';";
			viewAndMenu(query);
		}

	}

	public static void viewTopUseful(String driver) {
		// same as viewFeedback, except get all the car vins from driver and get their
		// feedbacks, then pair the feedbacks with their avg rating, group by usr
		System.out.println("Limit max results to: (provide number)");
		int n = 0;
		try {
			n = Integer.parseInt(Utils.getInput());
			if (n < 0)
				System.out.println("Invalid input. Must be greater than 0.");
			else {
				String query = "SELECT login, date, rating, text, fid, avgRating FROM Feedback F, (SELECT vin, AVG(rating) avgRating FROM Feedback GROUP BY vin) AF, (SELECT vin FROM Owns WHERE login = '"
						+ driver + "') V  WHERE F.vin = V.vin AND AF.vin = F.vin ORDER BY avgRating desc LIMIT " + n
						+ ";";
				// SELECT login, date, rating, text, fid, avgRating FROM Feedback F, (SELECT
				// vin, AVG(rating) avgRating FROM Feedback GROUP BY vin) AF, (SELECT vin FROM
				// Owns WHERE login = 'flash') V WHERE F.vin = V.vin AND AF.vin = F.vin ORDER BY
				// avgRating desc LIMIT 2;
				viewAndMenu(query);
			}

		} catch (Exception e) {
			System.out.println("Invalid input. Requires numerical entry (example: 3)");
		}
		// to get average rating:
		// (SELECT vin, AVG(rating) avgRating FROM Feedback GROUP BY vin) F
		// get vins of driver
		// (SELECT vin FROM Owns WHERE login = '" +driver+"') v
	}

	public static void giveFeedback(String vin) {
		// check database Owns to see if current user owns this vin
		String ownerQuery = "SELECT * FROM Owns WHERE login = '" + Utils.currentUser + "' AND vin = '" + vin + "';";
		try {
			ResultSet r = Utils.QueryHelper(ownerQuery, Utils.stmt);
			if (r.next())
				System.out.println("Can not give feedback on yourself.");
			r.close();
		} catch (Exception e) {
			System.out.println("Failed to connect, or bad input.");
		}
		// check database Feedback to see if current user already made a feedback for
		// this vin
		String alreadyQuery = "SELECT * FROM Feedback WHERE login = '" + Utils.currentUser + "' AND vin = '" + vin
				+ "';";
		try {
			ResultSet r1 = Utils.QueryHelper(alreadyQuery, Utils.stmt);
			if (r1.next()) {
				System.out.println("Can't give feedback a second time on the same vehicle.");
				r1.close();
			} else {
				System.out.println("Choose rating 1-10 (10 means excellent):");
				try {
					// rating
					int rating = Integer.parseInt(Utils.getInput());// could fail if input is not a number
					if (rating < 1 || rating > 10)
						throw new NumberFormatException();
					try {
						// text
						System.out.println(
								"Provide a review if desired (300 characters max, no special chars) or simply hit enter:");
						String text = Utils.getInput();
						if (text == null)
							text = "";
						if (!Utils.SanitizeInput(text, "[a-zA-Z.,! ]{0,300}")) {
							throw new RuntimeException();
						}
						// insert into feedback a new Feedback row after asking for input.
						java.util.Date date = new java.util.Date();
						java.sql.Date sqlDate = new java.sql.Date(date.getTime());
						String inStmt = "INSERT INTO Feedback (login, vin, date, rating, text) VALUES('"
								+ Utils.currentUser + "', '" + vin + "', '" + sqlDate + "', " + rating + ", '" + text
								+ "');";
						try {
							int r2 = Utils.UpdateHelper(inStmt, Utils.stmt);
							if (r2 == 1)
								System.out.println("Feedback submitted.");
							else {
								System.out.println("Feedback not submitted. May have submitted feedback before.");
							}
						} catch (Exception e) {
							System.out.println("Failed to connect, or bad input.");
						}
					} catch (Exception e) {
						System.out.println("Bad input. Text can't contain special characters. Max 300 characters.");
					}
				} catch (Exception e) {
					System.out.println("Bad input. Must provide integer from 1 to 10.");
				}
			}
		} 
		catch (Exception e) {
			System.out.println("Failed to connect, or bad input.");
		}
	}

	private static void viewAndMenu(String query) {
		ResultSet result;
		try {
			result = Utils.QueryHelper(query, Utils.stmt);

			// display feedback
			List<String> usrList = new ArrayList<String>();
			List<String> fidList = new ArrayList<String>();
			int row = 1;
			System.out.println("Num\tUser\tDate\tRating\tReview");
			System.out.println("___________________________________________");
			while (result.next()) {
				System.out.print(row + "\t");
				System.out.print(result.getString("login") + "\t");
				System.out.print(result.getString("date") + "\t");
				System.out.print(result.getString("rating") + "\t");
				System.out.print(result.getString("text") + "\n");
				usrList.add(result.getString("login"));
				fidList.add(result.getString("fid"));
				row++;
			}

			// Select a feedback number (left-most number) to rate it.
			System.out.println("Select a feedback (by left-most number) to rate it.");
			// Hit enter to go back to Vehicle Browser.
			System.out.println("Hit Enter with no entry to go back.");
			// if selected feedback is user's own feedback (Utils.currentUser.equals(that
			// line's login)), refuse, say they can't
			// rate own feedback.
			String in = Utils.getInput();
			if (in == null || in.equals("")) // exit feedback
			{
				end = true;
				return;
			}
			int inInt = 0;
			try {
				inInt = Integer.parseInt(in) - 1;
			} catch (Exception e) {
				System.out.println("Invalid input. Returning to Vehicle Browser.");
				return;
			}

			if (usrList.size() > inInt && inInt >= 0) // attempt to rate feedback
			{
				if (usrList.get(inInt).equals(Utils.currentUser))// cancel if own feedback
					System.out.println("Can't rate own feedback.");
				else {
					// rate feedback
					System.out
							.println("Please rate: (0) Useless (1) Useful (2) Very Useful. Or empty entry to cancel. ");
					String r = Utils.getInput();
					if (r == null || r.equals(""))
						System.out.println("Rating canceled");
					else {
						if (r.equals("0") || r.equals("1") || r.equals("2")) {
							int ri = Integer.parseInt(r);
							String update = "INSERT INTO Usefulness VALUES('" + Utils.currentUser + "', "
									+ fidList.get(inInt) + ", " + ri + ") ON DUPLICATE KEY UPDATE score = " + ri + ";";
							try {
								Utils.UpdateHelper(update, Utils.stmt);
								System.out.println("Feedback has been rated.");
							} catch (Exception e) {
								System.out.println("Failed to rate.");
							}
						} else
							System.out.println("Invalid rating.");
					}
				}
			} else {
				System.out.println("Specified user is not in this list.");
			}
		} catch (Exception e) {
			System.out.println("Failed to retrieve feedback.");
		}
	}
}
