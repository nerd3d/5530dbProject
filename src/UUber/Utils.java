package UUber;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Utils {
	public static String getInput() throws Exception{
		String str;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while((str = in.readLine()) == null && str.length() == 0);
		return str;
	}
	public static String getInputToLower() throws Exception{
		String str;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while((str = in.readLine()) == null && str.length() == 0);
		return str.toLowerCase();
	}
	public static String getInputToUpper() throws Exception{
		String str;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while((str = in.readLine()) == null && str.length() == 0);
		return str.toUpperCase();
	}
	//sanitize function to be implemented..
}
