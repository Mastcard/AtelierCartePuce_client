/**
 *
 */
package test;

import org.junit.Ignore;
import org.junit.Test;

import crypto.CryptoManager;
import crypto.EncodingEnum;

/**
 * The Class TestByteStringConversion.
 * 
 * @author Adrien
 */
public class TestMisc {
	
	@Test
	@Ignore
	public void testFromStringToByte() {
//		String pinCode = "AA-AA-AA-AA";
//		String[] pinCodeSplitted = pinCode.split("-");
//			
//		System.out.println(Long.parseLong(pinCodeSplitted[0], 16));
//		//System.out.println(pinCodeSplitted[0].getBytes().);
//		
//		byte test = (byte) 0xAA;
//		System.out.println(String.valueOf(test));
//
//		System.out.println(Byte.parseByte(pinCodeSplitted[0], 16));
//		
//		
//		byte[] pinCodeByte = {
//				(byte) Byte.parseByte(Integer.toHexString(Integer.parseInt(pinCodeSplitted[0]))),
//				(byte) Byte.parseByte(Integer.toHexString(Integer.parseInt(pinCodeSplitted[1]))),
//				(byte) Byte.parseByte(Integer.toHexString(Integer.parseInt(pinCodeSplitted[2]))),
//				(byte) Byte.parseByte(Integer.toHexString(Integer.parseInt(pinCodeSplitted[3])))
//			};
//		System.out.println(pinCodeByte);
		
		String userId = "254";
		System.out.println((byte) Long.parseLong(userId));
		
		byte userIdByte = 0x2f;
		System.out.println(String.valueOf(userIdByte));
		
		/*
		for (int i = testString.length() - 1; i >= 0 ; i--) {
			char hexaDigit = testString.charAt(i);
			String hexaDigitString = String.valueOf(hexaDigit);
			System.out.println(hexaDigitString);
			int hexaNumber = Integer.parseInt(hexaDigitString);
			somme += hexaNumber * (Math.pow(16, weight));
			weight++;
		}
		*/
	}
	
	@Test
	@Ignore
	public void testEncryptWithSeed() {
		int seed = 289;
		int salt1 = 512;
		int salt2 = 751;
		
		String message = "adrien";
		String derivedMessage = CryptoManager.derive(message, salt1, 20);
		String firstLevel = CryptoManager.encode(salt2 + "#" + derivedMessage, EncodingEnum.SHA_256);
		String secondLevel = CryptoManager.encode(salt1 + "#" + firstLevel, EncodingEnum.SHA_256);
		String encryptedMessage = CryptoManager.encode(seed + "#" + secondLevel, EncodingEnum.SHA_256);
		System.out.println(encryptedMessage.length());
	}
	
	@Test
	public void testEncryptWithoutSeed() {
		int salt1 = 384;
		int salt2 = 571;
		String test = "test";
		String test_password = "test_password";
		
		String derivedMessage = CryptoManager.derive(test, salt1, 20);
		String firstLevel = CryptoManager.encode(salt2 + "#" + derivedMessage, EncodingEnum.SHA_256);
		String secondLevel = CryptoManager.encode(salt1 + "#" + firstLevel, EncodingEnum.SHA_256);
		System.out.println(test + " => " + secondLevel);
		
		derivedMessage = CryptoManager.derive(test_password, salt1, 20);
		firstLevel = CryptoManager.encode(salt2 + "#" + derivedMessage, EncodingEnum.SHA_256);
		secondLevel = CryptoManager.encode(salt1 + "#" + firstLevel, EncodingEnum.SHA_256);
		System.out.println(test_password + " => " + secondLevel);
		
		String path = "/Users/Adrien/";
		String fileCryptedName = CryptoManager.encode(path, EncodingEnum.SHA_256);
		System.out.println(fileCryptedName);
		
		int seed = 524;
		derivedMessage = CryptoManager.derive(test, salt1, 20);
		firstLevel = CryptoManager.encode(salt2 + "#" + derivedMessage, EncodingEnum.SHA_256);
		secondLevel = CryptoManager.encode(salt1 + "#" + firstLevel, EncodingEnum.SHA_256);
		System.out.println(test + " ==> " + secondLevel);
		
		derivedMessage = CryptoManager.derive(test_password, salt1, 20);
		firstLevel = CryptoManager.encode(salt2 + "#" + derivedMessage, EncodingEnum.SHA_256);
		secondLevel = CryptoManager.encode(salt1 + "#" + firstLevel, EncodingEnum.SHA_256);
		String result = CryptoManager.encode(seed + "#" + secondLevel, EncodingEnum.SHA_256);
		System.out.println(test_password + " ==> " + result);
	}
	
}
