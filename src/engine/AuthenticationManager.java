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
public class AuthenticationManager {

	/** The log. */
	private static Logger log = Logger.getLogger(AuthenticationManager.class);
	
	/** The Constant MAX_PIC_CODE_TRY. */
	public final int MAX_PIN_CODE_TRY = 2;
	
	/** The instance. */
	private static AuthenticationManager instance = new AuthenticationManager();
	
	/** The seed. */
	private int seed;
	
	/** The salt 1. */
	private int salt1;
	
	/** The salt 2. */
	private int salt2;
	
	/** The login password is ok. */
	private boolean loginPasswordIsOk;
	
	/** The biometry is ok. */
	private boolean biometryIsOk;
	
	/** The pin code. */
	private byte[] pinCode;
	
	/** The added user id. */
	private String addedUserId;
	
	/**
	 * 
	 * Instantiates a new AuthenticationManager.
	 *
	 */
	private AuthenticationManager() {
		init();
	}
	
	/**
	 * Gets the instance.
	 * 
	 * @return the instance
	 */
	public static AuthenticationManager getInstance() {
		return instance;
	}
	
	/**
	 * Init.
	 */
	public void init() {
		log.info("Init authentication states");
		loginPasswordIsOk = false;
		biometryIsOk = false;
	}

	/**
	 * Sets the seed.
	 * 
	 * @param seed the seed to set
	 */
	public void setSeed(int seed) {
		this.seed = seed;
	}

	/**
	 * Sets the salt1.
	 * 
	 * @param salt1 the salt1 to set
	 */
	public void setSalt1(int salt1) {
		this.salt1 = salt1;
	}

	/**
	 * Sets the salt2.
	 * 
	 * @param salt2 the salt2 to set
	 */
	public void setSalt2(int salt2) {
		this.salt2 = salt2;
	}
	
	/**
	 * Gets the pinCode.
	 * 
	 * @return the pinCode
	 */
	public byte[] getPinCode() {
		return pinCode;
	}

	/**
	 * Sets the pinCode.
	 * 
	 * @param pinCode the pinCode to set
	 */
	public void setPinCode(byte[] pinCode) {
		this.pinCode = pinCode;
	}
	
	/**
	 * Gets the seed.
	 * 
	 * @return the seed
	 */
	public int getSeed() {
		return seed;
	}

	/**
	 * Gets the salt1.
	 * 
	 * @return the salt1
	 */
	public int getSalt1() {
		return salt1;
	}

	/**
	 * Gets the salt2.
	 * 
	 * @return the salt2
	 */
	public int getSalt2() {
		return salt2;
	}
	
	/**
	 * Gets the addedUserId.
	 * 
	 * @return the addedUserId
	 */
	public String getAddedUserId() {
		return addedUserId;
	}

	/**
	 * Sets the addedUserId.
	 * 
	 * @param addedUserId the addedUserId to set
	 */
	public void setAddedUserId(String addedUserId) {
		this.addedUserId = addedUserId;
	}

	/**
	 * Sets the loginPasswordIsOk.
	 * 
	 * @param loginPasswordIsOk the loginPasswordIsOk to set
	 */
	public void setLoginPasswordIsOk(boolean loginPasswordIsOk) {
		this.loginPasswordIsOk = loginPasswordIsOk;
	}
	
	/**
	 * Sets the biometryIsOk.
	 * 
	 * @param biometryIsOk the biometryIsOk to set
	 */
	public void setBiometryIsOk(boolean biometryIsOk) {
		this.biometryIsOk = biometryIsOk;
	}
	
	/**
	 * Is authentication ok.
	 * 
	 * @return authentication final state
	 */
	public boolean isAuthenticationOk() {
		return (loginPasswordIsOk && biometryIsOk);
	}


}
