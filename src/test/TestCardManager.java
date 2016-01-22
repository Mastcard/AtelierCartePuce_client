package test;

import javax.smartcardio.CardException;

import card.CardManager;

/**
 * 
 * @author lalatiana
 *
 */
public class TestCardManager {

	public static void main(String[] args) throws CardException {
		String a = "AA";  // 0xAA
		byte aByte = (byte) Long.parseLong(a, 16);
		
		String userId = "47";
		byte userIdByte = (byte) Long.parseLong(userId);
		
		
		byte pin[]={(byte)aByte,(byte)aByte,(byte)aByte,(byte)aByte};
		byte word[] = {(byte)0x00,(byte)0x00,(byte)0x00,(byte) userIdByte};
		
		CardManager.checkCard();
		CardManager.verify(pin);
		CardManager.read(pin);
		CardManager.update(pin, word);
		
		byte[] data = CardManager.read(pin);
		byte pinCode = data[3];
		System.out.println(String.valueOf(pinCode));
	}

}
