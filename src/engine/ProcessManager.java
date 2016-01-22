/**
 *
 */
package engine;

import java.net.UnknownHostException;

import javax.smartcardio.CardException;

import org.apache.log4j.Logger;

import card.CardManager;
import crypto.CryptoManager;
import crypto.EncodingEnum;
import network.NetworkCommunicator;

/**
 * The Class ProcessusManager.
 * 
 * @author Adrien
 */
public class ProcessManager {

	/** The log. */
	private static Logger log = Logger.getLogger(ProcessManager.class);
	
	/** The authentication manager. */
	private static AuthenticationManager authenticationManager = AuthenticationManager.getInstance();
	
	/** The biometry manager. */
	private static BiometryManager biometryManager = BiometryManager.getInstance();
	
	/**
	 * Waits for a card to be inserted.
	 * @throws CardException 
	 */
	public static void waitForCard() throws CardException {
		CardManager.checkCard();
		authenticationManager.init();
	}
	
	/**
	 * Waits the card to be ejected.
	 */
	public static void waitForCardEjection() {
		CardManager.checkCardEjection();
	}
	
	/**
	 * Unlocks card.
	 * 
	 * @param pin code
	 * @throws CardException 
	 */
	public static boolean unlockCard(String pinCode) throws CardException {
		boolean unlocked = true;
		
		String[] pinCodeSplitted = pinCode.split("-");
		if (pinCodeSplitted.length < 4) {
			log.error("Pin code in wrong format");
			unlocked = false;
		} else {
			byte[] pinCodeByte = {
					(byte) Long.parseLong(pinCodeSplitted[0], 16),
					(byte) Long.parseLong(pinCodeSplitted[1], 16),
					(byte) Long.parseLong(pinCodeSplitted[2], 16),
					(byte) Long.parseLong(pinCodeSplitted[3], 16)
				};
			
			unlocked = CardManager.verify(pinCodeByte);
			if (unlocked) {
				authenticationManager.setPinCode(pinCodeByte);
			}
		}
		
		return unlocked;
	}
	
	/**
	 * Read the user id.
	 * @throws CardException 
	 */
	public static String readUserId() throws CardException {
		byte[] data = CardManager.read(authenticationManager.getPinCode());
		byte userId = data[3];
		return String.valueOf(userId);
	}
	
	/**
	 * Retrieves the seed.
	 * 
	 * @param user id
	 * @throws UnknownHostException 
	 */
	public static void retrieveSeed(String userId) throws UnknownHostException {
		NetworkCommunicator.sendHello(userId);
		NetworkCommunicator.waitEndTreatmentStatus();
	}
	
	/**
	 * Sends login.
	 * 
	 * @param login
	 * @throws UnknownHostException 
	 */
	public static void sendLogin(String login) throws UnknownHostException {
		String encryptedLogin = encryptWithoutSeed(login);
		NetworkCommunicator.sendLogin(encryptedLogin);
	}
	
	/**
	 * Sends password.
	 * 
	 * @param password
	 * @return true if authentication on server worked, false otherwise
	 * @throws UnknownHostException 
	 */
	public static boolean sendPassword(String password) throws UnknownHostException {
		String encryptedPassword = encryptWithSeed(password);
		NetworkCommunicator.sendPassword(encryptedPassword);
		return NetworkCommunicator.waitEndTreatmentStatus();
	}
	
	/**
	 * Launches biometry program.
	 * 
	 * @return the biometry program result
	 */
	public static void launchBiometryProgram() {
		log.info("Launching the biometric recognition");
		
		/**
		 * use jni OR RuntimeExec
		 */
		
	}
	
	/**
	 * Send admin hello.
	 * @throws UnknownHostException
	 */
	public static void sendAdminHello() throws UnknownHostException {
		NetworkCommunicator.sendAdminHello();
		NetworkCommunicator.waitEndTreatmentStatus();
	}
	
	/**
	 * Add login.
	 * 
	 * @param login
	 * @param password
	 * @return false if login already exists, true otherwise
	 * @throws UnknownHostException 
	 */
	public static boolean addLoginPassword(String login, String password) throws UnknownHostException {
		String encryptedLogin = encryptWithoutSeed(login);
		String encryptedPassword = encryptWithoutSeed(password);
		NetworkCommunicator.sendAdminAdd(encryptedLogin, encryptedPassword);
		biometryManager.setCurrentUserFilePath(biometryManager.BIOMETRY_DIR_PATH + "user_" + encryptedLogin);
		return NetworkCommunicator.waitEndTreatmentStatus();
	}
	
	/**
	 * Add path.
	 * 
	 * @param path
	 * @throws UnknownHostException 
	 */
	public static String addPath(String path) throws UnknownHostException {
		String encryptedPath = CryptoManager.encode(path, EncodingEnum.SHA_256);
		NetworkCommunicator.sendAdminBio(encryptedPath);
		NetworkCommunicator.waitEndTreatmentStatus();
		biometryManager.updateFilesMap();
		return authenticationManager.getAddedUserId();
	}
	
	/**
	 * Save user id on card
	 * 
	 * @param user id
	 * @throws CardException 
	 */
	public static void saveUserIdOnCard(String userId) throws CardException {
		byte[] data = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) Long.parseLong(userId)};
		CardManager.update(authenticationManager.getPinCode(), data);
	}
	
	/**
	 * Encrypt with seed.
	 * 
	 * @param message
	 * @return derived and encrypted (with seed) message
	 */
	private static String encryptWithSeed(String message) {
		return encrypt(message, true);
	}
	
	/**
	 * Encrypt without seed.
	 * 
	 * @param message
	 * @return derived and encrypted (without seed) message
	 */
	private static String encryptWithoutSeed(String message) {
		return encrypt(message, false);
	}
	
	/**
	 * Encrypt.
	 * 
	 * @param message
	 * @param with or without seed
	 * @return the encrypted message
	 */
	private static String encrypt(String message, boolean withSeed) {
		int seed = authenticationManager.getSeed();
		int salt1 = authenticationManager.getSalt1();
		int salt2 = authenticationManager.getSalt2();
		String result = "";
		
		String derivedMessage = CryptoManager.derive(message, salt1, 20);
		String firstLevel = CryptoManager.encode(salt2 + "#" + derivedMessage, EncodingEnum.SHA_256);
		String secondLevel = CryptoManager.encode(salt1 + "#" + firstLevel, EncodingEnum.SHA_256);
		
		if (withSeed) {
			result = CryptoManager.encode(seed + "#" + secondLevel, EncodingEnum.SHA_256);
		} else {
			result = secondLevel;
		}
		
		log.debug("Message encrypted with seed : " + result);
		return result;
	}
	
}
