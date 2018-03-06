package UUber;

public class UserUI {
	public static void ShowMenu() throws Exception{
		while (true) {
			// welcome and list options...wait for input
			System.out.println("*** User Relations ***");
			System.out.println("Please make a selection:");
			System.out.println("1. Search Users");
			System.out.println("2. Trusted Users");
			System.out.println("3. Mistrusted Users");
			System.out.println("4. Back");
			switch (Utils.getInput()) {
			case "1":
				UserBrowserUI.ShowMenu();
				System.out.println("not hooked up yet");
				break;
			case "2":
				// DisplayTrusted();
				System.out.println("not hooked up yet");
				break;
			case "3":
				// DisplayMistrusted()
				System.out.println("not hooked up yet");
				break;
			case "4":
				return;
			default:
				System.out.println("Invalid input.");
				break;
			}
		}
	}
}
