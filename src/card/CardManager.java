package card;
import java.util.List;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

import org.apache.log4j.Logger;
/**
 * 
 * @author lalatiana
 *
 */
public class CardManager {
	
	/** The log. */
	private static Logger log = Logger.getLogger(CardManager.class);
	
 	private static CardTerminal terminal;
    private static Card card;
    private static int i;
    	    
    public static List<CardTerminal> getTerminals() throws CardException {
        return TerminalFactory.getDefault().terminals().list();

    }
    
    public static String toString(byte[] byteTab){
        String texte="";
        String hexNombre;
        for(i=0;i<byteTab.length;i++){
                hexNombre="";
                hexNombre=Integer.toHexString(byteTab[i]);
                if(hexNombre.length()==1){
                    texte+=" 0"+hexNombre;
                }
                else{
                    texte+=" "+hexNombre;
                }
        }
        return texte;
    }
    
    /**
     * checkCard
     * Vérifier s'il y a une carte dans le terminal
     * @throws CardException 
     */
    public static void checkCard() throws CardException{
    	boolean cardPresent = false;
    	
		log.info("Waiting for a card to be inserted...");
    	while (!cardPresent) {
    		 try{
    			List<CardTerminal> terminauxDispos = getTerminals();
		        terminal = terminauxDispos.get(0);
		        cardPresent = terminal.waitForCardPresent(1000);
    
		        Thread.sleep(1000);
    		 } catch(Exception e) {
    			 
    		 }
	        	
	     }
    	log.info("Card inserted");
    }
    
    public static void checkCardEjection() {
    	boolean cardEjected = false;
    	
		log.info("Waiting for the card to be ejected...");
    	while (!cardEjected) {
    		 try{
    			List<CardTerminal> terminauxDispos = getTerminals();
		        terminal = terminauxDispos.get(0);
		        cardEjected = terminal.waitForCardAbsent(1000);
    
		        Thread.sleep(1000);
    		 } catch(Exception e) {
    			 
    		 }
	        	
	     }
    	log.info("Card ejected");
    }
    
    /**
     *verify
     *pour vérifier le code PIN
     * @param pin: le code pin dans un tableau de byte
     * @throws CardException
     */
    
    public static boolean verify(byte[] pin) throws CardException{
    	boolean pinIsCorrect = false;
    	
    	//Connexion à la carte
        card = terminal.connect("T=0");
        //ATR (answer To Reset)
        log.debug("ATR: " + toString(card.getATR().getBytes()));
        log.debug("PROTOCOL: " + card.getProtocol());
        
	    CardChannel channel = card.getBasicChannel();
	    CommandAPDU pinCommande ;
	    ResponseAPDU r;
	         
	    byte accesPin[] = {0x00,(byte) 0x20,(byte)0x00,(byte)0x07,(byte)0x04};
	    byte apdu[]= new byte[accesPin.length + pin.length];
	         
	    System.arraycopy(accesPin,0,apdu,0,accesPin.length);
	    System.arraycopy(pin,0,apdu,accesPin.length,pin.length);
	         
	    pinCommande = new CommandAPDU(apdu);//test code pin
	       
	    r = channel.transmit(pinCommande);
	    String SW1 = Integer.toHexString(r.getSW1());
	    String SW2 = Integer.toHexString(r.getSW2());
	    log.debug("test: " + "SW1 : "+SW1+" SW2 : "+SW2);
	            
	    if(SW1.equals("90") && SW2.equals("0")){
	    	log.debug("PIN ACCEPTE : " + "SW1 : 0x"+Integer.toHexString(r.getSW1())+" SW2 : 0x"+Integer.toHexString(r.getSW2()));
	    	pinIsCorrect = true;
	    }
	    else{
	    	log.debug("PIN REFUSE : " + "SW1 : 0x"+Integer.toHexString(r.getSW1())+" SW2 : 0x"+Integer.toHexString(r.getSW2()));
	    }
	        
	   card.disconnect(false);
	   return pinIsCorrect;
    }
    
    /**
     * read
     * pour lire le contenu de la carte
     * @param pin: le code pin dans un tableau de byte
     * @throws CardException
     */
    public static byte[] read (byte[] pin) throws CardException{
    	byte[] data = null;
    	
    	//Connexion à la carte
        card = terminal.connect("T=0");
        //ATR (answer To Reset)
        log.debug("ATR: " + toString(card.getATR().getBytes()));
        log.debug("PROTOCOL: " + card.getProtocol());
    	
        CardChannel channel = card.getBasicChannel();
        CommandAPDU readCommande,pinCommande ;
        ResponseAPDU r;
        
        byte accesPin[] = {0x00,(byte) 0x20,(byte)0x00,(byte)0x07,(byte)0x04};
        byte apdu[]= new byte[accesPin.length + pin.length];
        
        System.arraycopy(accesPin,0,apdu,0,accesPin.length);
        System.arraycopy(pin,0,apdu,accesPin.length,pin.length);
        
        pinCommande = new CommandAPDU(apdu);//test code pin
        r = channel.transmit(pinCommande);
        log.debug("reponse pin : " + "SW1 : 0x"+Integer.toHexString(r.getSW1())+" SW2 : 0x"+Integer.toHexString(r.getSW2()));
        
        readCommande = new CommandAPDU(0x80,0xBE,0x00,0x10,0x04);//lecture
        r = channel.transmit(readCommande);
        log.debug("RESPONSE : " + "SW1 : 0x"+Integer.toHexString(r.getSW1())+" SW2 : 0x"+Integer.toHexString(r.getSW2())+" DATA : "+toString(r.getData()));
        data = r.getData();
        
        card.disconnect(false);
        return data;
    }
    
    /**
     * update
     * pour écrire sur la carte
     * @param pin : le code pin dans un tableau de byte
     * @param word : le mot à écrire sur la carte dans un tableau de bytes
     * @throws CardException
     */
    public static void update (byte[] pin,byte[] word) throws CardException{
    	
    	//Connexion à la carte
        card = terminal.connect("T=0");
        //ATR (answer To Reset)
        log.debug("ATR: " + toString(card.getATR().getBytes()));
        log.debug("PROTOCOL: " + card.getProtocol());
        
        CardChannel channel = card.getBasicChannel();
        CommandAPDU writeCommande,pinCommande ;
        ResponseAPDU r;
        
        byte accesPin[] = {0x00,(byte) 0x20,(byte)0x00,(byte)0x07,(byte)0x04};
        byte apdu[]= new byte[accesPin.length + pin.length];
        
        System.arraycopy(accesPin,0,apdu,0,accesPin.length);
        System.arraycopy(pin,0,apdu,accesPin.length,pin.length);
        
        pinCommande = new CommandAPDU(apdu);//test code pin
        r = channel.transmit(pinCommande);
        log.debug("reponse pin : " + "SW1 : 0x"+Integer.toHexString(r.getSW1())+" SW2 : 0x"+Integer.toHexString(r.getSW2()));
        
        byte update[] = {(byte)0x80,(byte)0xDE,(byte)0x00,(byte)0x10,(byte)0x04};
        byte in[]= new byte[update.length + word.length];
        
        System.arraycopy(update,0,in,0,update.length);
        System.arraycopy(word,0,in,update.length,word.length);
        
        writeCommande = new CommandAPDU(in);//ecriture
        r = channel.transmit(writeCommande);
        log.debug("RESPONSE write: " + "SW1 : 0x"+Integer.toHexString(r.getSW1())+" SW2 : 0x"+Integer.toHexString(r.getSW2()));
       
        card.disconnect(false);
    }
}
