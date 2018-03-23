package UUber;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FeedbackUI {

	public static void viewFeedback(String vin) {
		while(true)
		{
			//get all feedbacks for this vin
			String query = "SELECT login, date, rating, text, fid FROM Feedback WHERE vin = '" + vin + "';";
			ResultSet result;
			try {
				result = Utils.QueryHelper(query, Utils.stmt);
				
				//display feedback
				List<String> usrList = new ArrayList<String>();
				List<String> fidList = new ArrayList<String>();
				int row = 1;
				System.out.println("Num\tUser\tDate\tRating\tReview");
				System.out.println("___________________________________________");
				while(result.next()) 
				{
					System.out.print(row + "\t");
					System.out.print(result.getString("login") + "\t");
					System.out.print(result.getString("date") + "\t");
					System.out.print(result.getString("rating") + "\t");
					System.out.print(result.getString("text") + "\n");
					usrList.add(result.getString("login"));
					fidList.add(result.getString("fid"));
					row++;
				}
				
				//Select a feedback number (left-most number) to rate it.
				System.out.print("Select a feedback (by left-most number) to rate it.");
				//Hit enter to go back to Vehicle Browser.
				System.out.print("Hit Enter with no entry to go back.");
				//if selected feedback is user's own feedback (Utils.currentUser.equals(that line's login)), refuse, say they can't
				//rate own feedback.
				String in = Utils.getInput();
				if(in == null || in.equals("")) //exit feedback
					return;				
				int inInt = 0;
				try {
					inInt = Integer.parseInt(in)-1;
				}
				catch(Exception e){System.out.println("Invalid input. Returning to Vehicle Browser."); return;}
				
				if(usrList.size() > inInt && inInt >= 0) //attempt to rate feedback
				{  
					if(usrList.get(inInt).equals(Utils.currentUser))//cancel if own feedback
						System.out.print("Can't rate own feedback.");
					else
					{
						//rate feedback
						System.out.println("Please rate: (0) Useless (1) Useful (2) Very Useful. Or empty entry to cancel. ");
						String r = Utils.getInput();
						if(r == null || r.equals(""))
							System.out.println("Rating canceled");
						else
						{
							if(r.equals("0") || r.equals("1") || r.equals("2"))
							{
								int ri = Integer.parseInt(r);
								String update = "INSERT INTO Usefulness VALUES('"+Utils.currentUser+"', " +fidList.get(inInt)+", "+ri+") ON DUPLICATE KEY UPDATE score = " + ri +";";
								try{Utils.UpdateHelper(update, Utils.stmt);
									System.out.println("Feedback has been rated.");}
								catch(Exception e) {System.out.println("Failed to rate.");}
							}
							else
								System.out.println("Invalid rating.");	
						}
					}
				}
				else {
					System.out.print("Specified user is not in this list.");
				}
			}
			catch(Exception e) {System.out.println("Failed to retrieve feedback.");}	
		}

	}
	public static void viewTopUseful(String driver) 
	{
		//same as viewFeedback, except get all the car vins from driver and get their feedbacks, then pair the feedbacks with their avg rating, group by usr
		//String query = "SELECT login, date, rating, text, fid FROM Feedback WHERE vin = '" + vin + "';";
		//to get average rating:
		//(SELECT vin, AVG(rating) avgRating FROM Feedback GROUP BY vin) F
	}
	public static void giveFeedback(String vin) {
		//check database Owns to see if current user owns this vin
		//check database Feedback to see if current user already made a feedback for this vin
		
		//insert into feedback a new Feedback row after asking for input.
	}
}
