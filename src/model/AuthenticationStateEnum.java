/**
 *
 */
package model;

/**
 * The Class AuthentificationState.
 * 
 * @author Adrien
 */
public enum AuthenticationStateEnum {

	WAIT ("Insert your ID card"),
	CARD_ID ("Tape your card PIN code..."),
	LOGIN ("Tape your login..."),
	PASSWORD ("Tape your password..."),
	BIOMETRY ("Place yourself at a good distance, and take a photo...");
	
	/** The value. */
	private String value = "";
	
	/**
	 * Instantiates a new AuthenticationStateEnum.
	 * 
	 */
	AuthenticationStateEnum(String value) {
		this.value = value;
	}
	
	/**
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		return value;
	}
	
}
