/**
 *
 */
package main;

import java.net.UnknownHostException;
import java.util.Scanner;

import javax.smartcardio.CardException;

import engine.AuthenticationManager;
import engine.BiometryManager;
import engine.ProcessManager;

/**
 * The Class MainConsole.
 * 
 * @author Adrien
 */
public class MainConsole {

	private static final String ADMIN_PASSWORD = "admin_password";

	private static AuthenticationManager authenticationManager = AuthenticationManager.getInstance();
	private static BiometryManager biometryManager = BiometryManager.getInstance();
	
	/**
	 * MAIN.
	 * @throws CardException 
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws CardException, UnknownHostException {
		Scanner scanner = new Scanner(System.in);
		String inputString = "";
		
		// Launch server
		//UDPServer.getInstance().start();
		
		while (true) {
			System.out.println("Choose mode (tape 1 or 2) :");
			System.out.println("1 - authentication | 2 - administration");
			inputString = scanner.nextLine();
			
			switch (inputString.charAt(0)) {
				case '1':
					System.out.println("Authentication mode is starting...");
					authenticationMode();
					break;
				case '2':
					//inputString = new String(System.console().readPassword("ENTER ADMINISTRATOR PASSWORD : "));
					System.out.print("ENTER ADMINISTRATOR PASSWORD : ");
					inputString = scanner.nextLine();
					
					if (!inputString.equals(ADMIN_PASSWORD)) {
						System.out.println("\tWrong admin password");
						break;
					}
					System.out.println("Administration mode is starting...");
					administrationMode();
					break;
				default:
					System.out.println("\tERROR : tape 1 or 2");
					break;
			}
			System.out.println();
		}

	}
	
	private static void authenticationMode() throws CardException, UnknownHostException {
		Scanner scanner = new Scanner(System.in);
		
		// Insert card and tape pin code
		if (!insertCardAndTapePinCode()) {
			return;
		}
		
		// Retrieve user id from card
		String userId = ProcessManager.readUserId();
		
		// Send Hello in order to retrieve seed and salts
		ProcessManager.retrieveSeed(userId);
		
		// Ask for login and send it
		System.out.print("Enter your login : ");
		String login = scanner.nextLine();
		ProcessManager.sendLogin(login);
		
		// Ask for password and send it
		System.out.println("Enter your password : ");
		String password = scanner.nextLine();
		boolean authenticationOnServerSuccessful = ProcessManager.sendPassword(password);
		authenticationManager.setLoginPasswordIsOk(authenticationOnServerSuccessful);
		
		// Launch biometry program
		System.out.println("Biometric calculation...");
		ProcessManager.launchBiometryProgram();
		/**
		 * update authentication status for biometric in AuthenticationManager.
		 */
		
		// Final authentication status
		if (authenticationManager.isAuthenticationOk()) {
			System.out.println("WELCOME " + login + ", you have been recognized !");
		} else {
			System.out.println("ACCESS DENIED ! Authentication failed ! ");
		}
		
	}
	
	private static void administrationMode() throws CardException, UnknownHostException {
		Scanner scanner = new Scanner(System.in);
		
		// Insert card and tape pin code
		if (!insertCardAndTapePinCode()) {
			return;
		}
		
		// Send Admin_hello in order to retrieve the salts
		ProcessManager.sendAdminHello();
		
		// Ask login and password
		System.out.println("USER CREATION");
		boolean loginAvailable = false;
		while (!loginAvailable) {
			System.out.print("Enter a login : ");
			String login = scanner.nextLine();
			System.out.print("Enter a password : ");
			String password = scanner.nextLine();
			loginAvailable = ProcessManager.addLoginPassword(login, password);
			if (!loginAvailable) {
				System.out.println("Error : login not available");
			}
		}
		
		// Launch biometric calculation
		System.out.println("Biometric calculation...");
		ProcessManager.launchBiometryProgram();
		
		// Send the result file path
		String userId = ProcessManager.addPath(biometryManager.getCurrentUserFilePath());
		
		// Write id on the card
		ProcessManager.saveUserIdOnCard(userId);
		
		System.out.println("User addition and save on card successful !");
	}
	
	/**
	 * 
	 * 
	 * UTIL
	 * 
	 * 
	 */
	
	private static boolean insertCardAndTapePinCode() throws CardException {
		Scanner scanner = new Scanner(System.in);
		String inputStream = "";

		// Insert card
		System.out.println("Insert the ID card you want to activate...");
		ProcessManager.waitForCard();
		
		// Tape card id
		int tryCount = 0;
		boolean unlocked = false;
		while (tryCount < authenticationManager.MAX_PIN_CODE_TRY && !unlocked) {
			System.out.print("Tape the card pin code (" + (authenticationManager.MAX_PIN_CODE_TRY-tryCount) + " time(s) left): ");
			inputStream = scanner.nextLine();
			unlocked = ProcessManager.unlockCard(inputStream);
			if (!unlocked) {
				System.out.println("\tWrong pin code !");
				tryCount++;
			}
		}
		if (!unlocked) {
			System.out.println("Too much fails => eject the card");
			ProcessManager.waitForCardEjection();
			return false;
		}
		
		return true;
	}
	
	
	
}
