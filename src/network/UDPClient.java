/**
 *
 */
package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * The Class UDPClient.
 * 
 * @author Adrien
 */
public class UDPClient implements Runnable {

    /** The log. */
    private static Logger log = Logger.getLogger(UDPClient.class);

    /** The Constant BUFFER_SIZE. */
    private static final int BUFFER_SIZE = 1024;

    /** The buffer. */
    private byte buffer[] = new byte[BUFFER_SIZE];

    /** The ip. */
    private InetAddress ip;

    /** The port. */
    private int port;

    /** The message to send. */
    private String messageToSend;

    /** The response. */
    private String response;

    /**
     * Instantiates a new UDPClient.
     */
    public UDPClient() {
    }

    /**
     * Instantiates a new UDPClient.
     *
     * @param ip
     * @param port
     * @param messageToSend
     */
    public UDPClient(String ip, int port, String messageToSend) throws UnknownHostException {
        this.ip = InetAddress.getByName(ip);
        this.port = port;
        this.messageToSend = messageToSend;
    }

    @Override
    public void run() {
        try {
            buffer = this.messageToSend.getBytes("UTF-8");
            int length = buffer.length;

            // Datagram socket.
            log.debug("Creating the DatagramSocket...");
            DatagramSocket socket = new DatagramSocket(5554, ip);
            //socket.bind(new InetSocketAddress(ip, port));
            log.debug("Done.");

            // Datagram packet.
            log.debug("Creating the datagram packet with :");
            log.debug("\t\tip = " + ip);
            log.debug("\t\tport = " + port);
            log.debug("\t\tbuffer = \"" + Arrays.toString(buffer) + "\"");
            log.debug("\t\tsize = " + length);
            DatagramPacket dataToSend = new DatagramPacket(buffer, length, ip, port);
            log.debug("Done.");

            // Send.
            log.info("Sending...");
            socket.send(dataToSend);
            log.debug("Done.");

            try {
            	// Read response
            	DatagramPacket dataToReceive = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
            	log.debug("Reading response...");
                socket.receive(dataToReceive);
                response = new String(dataToReceive.getData()).substring(0, dataToReceive.getLength());
                NetworkCommunicator.executeResponse(response);
                
            } catch(SocketException e) {
                e.printStackTrace();
                response = "";
            } finally {
                log.debug("Received : \"" + response + "\"");
                log.debug("Closing the DatagramSocket");
                socket.close();
                log.debug("Done.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /******************************************/

    /**
     * Gets the ip.
     *
     * @return the ip
     */
    public InetAddress getIp() {
        return ip;
    }

    /**
     * Sets the ip.
     *
     * @param ip
     */
    public void setIp(String ip) throws UnknownHostException {
        this.ip = InetAddress.getByName(ip);
    }

    /**
     * Gets the port.
     *
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the port.
     *
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Gets the message to send.
     *
     * @return the message to send
     */
    public String getMessageToSend() {
        return messageToSend;
    }

    /**
     * Sets the message to send.
     *
     * @param messageToSend
     */
    public void setMessageToSend(String messageToSend) {
        this.messageToSend = messageToSend;
    }

    /**
     * Gets the response.
     *
     * @return the response
     */
    public String getResponse() {
        return response;
    }

}