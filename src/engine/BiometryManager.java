/**
 *
 */
package engine;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import crypto.CryptoManager;
import crypto.EncodingEnum;

/**
 * The Class BiometryManager.
 * 
 * @author Adrien
 */
public class BiometryManager {

	/** The log. */
	private static Logger log = Logger.getLogger(BiometryManager.class);
	
	/** The instance. */
	private static BiometryManager instance = new BiometryManager();
	
	/** The Constant BIOMETRY_DIR_PATH. */
	public final String BIOMETRY_DIR_PATH = "/Users/Adrien/Desktop/";

	/** The files. */
	private Map<String, String>files = new HashMap<String, String>();
	
	/** The current user file path. */
	private String currentUserFilePath;
	
	/**
	 * 
	 * Instantiates a new BiometryManager.
	 *
	 */
	private BiometryManager() {
		updateFilesMap();
	}
	
	/**
	 * Gets the instance.
	 * 
	 * @return the instance
	 */
	public static BiometryManager getInstance() {
		return instance;
	}
	
	/**
	 * Updates files map.
	 */
	public void updateFilesMap() {
		File dir = new File(BIOMETRY_DIR_PATH);
		if (dir.exists()) {
			File[] listOfFiles = dir.listFiles();
			for (int i = 0; i < listOfFiles.length; i++) {
				File file = listOfFiles[i];
				if (!file.isDirectory()) {
					String filename = listOfFiles[i].getName();
					String fileCryptedName = CryptoManager.encode(filename, EncodingEnum.SHA_256);
					files.put(fileCryptedName, filename);
					log.debug("Add in the BIO_MANAGER : " + fileCryptedName + " -> " + filename);
				}
			}
		} else {
			log.error("The directory " + BIOMETRY_DIR_PATH + " does not exist !");
		}
	}
	
	/**
	 * Find file by crypted
	 * 
	 * @param encrypted name
	 * @return the file name
	 */
	public String findFileByEncryptedName(String encryptedName) {
		String filename = "";
		if (files.containsKey(encryptedName)) {
			filename = files.get(encryptedName);
		} else {
			log.error("File with encrypted name " + encryptedName + " not found");
		}
		return filename;
	}
	
	/**
	 * Launch biometry program.
	 */
	public void launchBiometryProgram() {
		log.debug("Biometry program with file \"" + currentUserFilePath + "\"");
		/**
		 * TODO call the program using currentUserFilePath
		 */
	}
	
	/**
	 * Sets the current user file path.
	 * 
	 * @param current user file path
	 */
	public void setCurrentUserFilePath(String currentUserFilePath) {
		this.currentUserFilePath = currentUserFilePath;
	}
	
	/**
	 * Gets the currentUserFilePath.
	 * 
	 * @return the currentUserFilePath
	 */
	public String getCurrentUserFilePath() {
		return currentUserFilePath;
	}
	
}
