/**
 *
 */
package network;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import crypto.CryptoManager;
import engine.AuthenticationManager;
import engine.BiometryManager;
import util.Constants;

/**
 * The Class NetworkCommunicator.
 * 
 * @author Adrien
 */
public class NetworkCommunicator {

	/** The log. */
	private static Logger log = Logger.getLogger(NetworkCommunicator.class);
	
	/** The authentication manager. */
	private static AuthenticationManager authenticationManager = AuthenticationManager.getInstance();
	
	/** The biometry manager. */
	private static BiometryManager biometryManager = BiometryManager.getInstance();
	
	/** The SERVER IP. */
	private static final String SERVER_IP = "127.0.0.1";
	
	/** The SERVER PORT. */
	private static final int SERVER_PORT = 5555;
	
	/** The can continue. */
	private static volatile boolean executionFinished;
	
	/** The well finished. */
	private static volatile boolean wellFinished;
	
	/**
	 * Execute response.
	 * 
	 * @param response
	 */
	public static synchronized void executeResponse(String response) {
		String[] responseSplitted = response.split(" ");
		String responsePrefix = responseSplitted[0];
		String responseSuffix = ""; 
		wellFinished = true;
		
		switch (responsePrefix) {
			case Constants.MESSAGE_PREFIX_HELLO:
				String hiddenSeedAndSalts = responseSplitted[1];
				String seed = CryptoManager.revealSeed(hiddenSeedAndSalts);
				String salt1 = CryptoManager.revealSalt1(hiddenSeedAndSalts);
				String salt2 = CryptoManager.revealSalt2(hiddenSeedAndSalts);
				authenticationManager.init();
				log.debug("Init authentification states");
				authenticationManager.setSeed(Integer.parseInt(seed));
				authenticationManager.setSalt1(Integer.parseInt(salt1));
				authenticationManager.setSalt2(Integer.parseInt(salt2));
				log.debug("Seed and salts updated");
				break;
			case Constants.MESSAGE_PREFIX_LOGIN:
				log.debug("Login have been well received");
				break;
			case Constants.MESSAGE_PREFIX_PASSW:
				String message = responseSplitted[1];
				if (message.equals("ko")) {
					wellFinished = false;
					log.info("Authentification error on server");
				} else {
					String filePath = biometryManager.findFileByEncryptedName(message);
					biometryManager.setCurrentUserFilePath(filePath);
				}
				break;
			case Constants.MESSAGE_PREFIX_ADMIN_HELLO:
				responseSuffix = responseSplitted[1];
				log.debug("Received salts");
				String receivedSalt1 = CryptoManager.revealSalt1(responseSuffix);
				String receivedSalt2 = CryptoManager.revealSalt2(responseSuffix);
				authenticationManager.setSalt1(Integer.parseInt(receivedSalt1));
				authenticationManager.setSalt2(Integer.parseInt(receivedSalt2));
				log.debug("Salts updated");
				break;
			case Constants.MESSAGE_PREFIX_ADMIN_ADD:
				responseSuffix = responseSplitted[1];
				if (responseSuffix.equals("ko")) {
					wellFinished = false;
					log.error("Login already used");
				} else {
					log.debug("User addition worked well...");
				}
				break;
			case Constants.MESSAGE_PREFIX_ADMIN_BIO:
				responseSuffix = responseSplitted[1];
				if (responseSuffix.equals("-1")) {
					wellFinished = false;
					log.error("User addition failed");
				} else {
					log.info("User addition successful");
					authenticationManager.setAddedUserId(responseSuffix);
					log.info("User id : " + authenticationManager.getAddedUserId());
				}
				break;
				
			default:
				log.error("Unknown response prefix : " + responsePrefix);
		}
		executionFinished = true;
	}
	
	/**
	 * Send hello.
	 * 
	 * @param userId
	 * @throws UnknownHostException 
	 */
	public static void sendHello(String userId) throws UnknownHostException {
		sendUDPRequest(Constants.MESSAGE_PREFIX_HELLO + " " + userId);
	}
	
	/**
	 * Send login.
	 * 
	 * @param login
	 * @throws UnknownHostException
	 */
	public static void sendLogin(String login) throws UnknownHostException {
		sendUDPRequest(Constants.MESSAGE_PREFIX_LOGIN + " " + login);
	}
	
	/**
	 * Send password.
	 * 
	 * @param password
	 * @throws UnknownHostException
	 */
	public static void sendPassword(String password) throws UnknownHostException {
		sendUDPRequest(Constants.MESSAGE_PREFIX_PASSW + " " + password);
	}
	
	/**
	 * Send admin add.
	 * 
	 * @param login
	 * @param password
	 * @throws UnknownHostException 
	 */
	public static void sendAdminHello() throws UnknownHostException {
		sendUDPRequest(Constants.MESSAGE_PREFIX_ADMIN_HELLO);
	}
	
	/**
	 * Send admin add.
	 * 
	 * @param login
	 * @param password
	 * @throws UnknownHostException 
	 */
	public static void sendAdminAdd(String login, String password) throws UnknownHostException {
		sendUDPRequest(Constants.MESSAGE_PREFIX_ADMIN_ADD + " " + login + " " + password);
	}
	
	/**
	 * Send admin bio.
	 * 
	 * @param path
	 * @throws UnknownHostException 
	 */
	public static void sendAdminBio(String path) throws UnknownHostException {
		sendUDPRequest(Constants.MESSAGE_PREFIX_ADMIN_BIO + " " + path);
	}

	/**
	 * Wait end treatment.
	 * 
	 * @return true if treatment ended well, false otherwise
	 */
	public static boolean waitEndTreatmentStatus() {
		log.info("Waiting for execution of response to be finished");
		while (!executionFinished) {
		}
		return wellFinished;
	}

	/**
	 * Send UDP request.
	 * 
	 * @param message
	 * @throws UnknownHostException
	 */
	private static void sendUDPRequest(String message) throws UnknownHostException {
		UDPClient udpClient = prepareUDPClient();
		udpClient.setMessageToSend(message);
		
		log.info("UDP client with message " + udpClient.getMessageToSend());
		executionFinished = false;
		new Thread(udpClient).start();
		log.info("UDP client sent successfully");
	}
	
	/**
	 * Prepare UDP client.
	 * 
	 * @throws UnknownHostException 
	 */
	private static UDPClient prepareUDPClient() throws UnknownHostException {
		UDPClient udpClient = new UDPClient();
		udpClient.setIp(SERVER_IP);
		udpClient.setPort(SERVER_PORT);
		return udpClient;
	}
	
}
