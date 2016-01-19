/**
 *
 */
package engine;

import org.apache.log4j.Logger;

/**
 * The Class AuthentificationManager.
 * 
 * @author Adrien
 */
public class AuthentificationManager {

	/** The log. */
	private static Logger log = Logger.getLogger(AuthentificationManager.class);
	
	/** The instance. */
	private static AuthentificationManager instance = new AuthentificationManager();
	
	/** The seed. */
	private int seed;
	
	/** The salt 1. */
	private int salt1;
	
	/** The salt 2. */
	private int salt2;
	
	/** The card pin is ok. */
	private boolean cardPinIsOk;
	
	/** The login password is ok. */
	private boolean loginPasswordIsOk;
	
	/** The biometry is ok. */
	private boolean biometryIsOk;
	
	/**
	 * 
	 * Instantiates a new AuthentificationManager.
	 *
	 */
	private AuthentificationManager() {
		init();
	}
	
	/**
	 * Gets the instance.
	 * 
	 * @return the instance
	 */
	public static AuthentificationManager getInstance() {
		return instance;
	}
	
	/**
	 * Init.
	 */
	public void init() {
		log.info("Init authentication states");
		cardPinIsOk = false;
		loginPasswordIsOk = false;
		biometryIsOk = false;
	}
	
	/**
	 * Waits for a card to be inserted.
	 */
	public void waitForCard() {
		/**
		 * TODO use card methods
		 */
	}
	
	/**
	 * Unlocks card.
	 * 
	 * @param pin code
	 */
	public void unlockCard(String pinCode) {
		/**
		 * TODO use card methods
		 */
	}
	
	/**
	 * Retrieves the seed.
	 */
	public void retrieveSeed(String userId) {
		/**
		 * TODO use client-server
		 */
		
		/**
		 * TODO uncrypt received message to have salts and seed
		 */
	}
	
	/**
	 * Sends login.
	 * 
	 * @param login
	 */
	public void sendLogin(String login) {
		/**
		 * TODO use client-server
		 */
	}
	
	/**
	 * Sends password.
	 */
	public void sendPassword(String password) {
		/**
		 * use crypto.jar
		 */
		
		/**
		 * use client-server
		 */
	}
	
	/**
	 * Launches biometry program.
	 */
	public void launchBiometryProgram() {
		/**
		 * use jni
		 */
	}
	
	/**
	 * Is authentication ok.
	 * 
	 * @return authentication final state
	 */
	public boolean isAuthenticationOk() {
		return (cardPinIsOk && loginPasswordIsOk && biometryIsOk);
	}

}
