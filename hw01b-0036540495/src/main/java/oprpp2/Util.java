package oprpp2;

import oprpp2.messages.AckMessage;
import oprpp2.messages.UDPMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for {@link oprpp2.Client} and {@link oprpp2.Server}
 * containing common functionality needed by both.
 */
public class Util {

    /**
     * Sends the given message to the server and waits for an ACK message.
     *
     * @param message the message to send
     * @param address the destination address
     * @param uid the unique identifier of the client
     * @param socket the socket to use for sending the message
     * @param numberOfTries the number of tries to send the message
     * @param millisToWait the number of milliseconds to wait for an ACK message during each try
     * @param ackMessageQueue the queue to poll for ACK messages
     * @param verbose whether to print debug messages
     * @return true if the message was sent and an ACK message was received, false otherwise
     */
    public static boolean sendAndWaitForAck(
            UDPMessage message, SocketAddress address, long uid,
            DatagramSocket socket, int numberOfTries, long millisToWait,
            LinkedBlockingQueue<AckMessage> ackMessageQueue, boolean verbose
    ) {
        // Serialize the message
        byte[] buffer;
        try {
            buffer = message.serialize();
        } catch (IOException e) {
            System.err.println("Failed to serialize the OUTMSG message: " + e.getMessage());
            return false;
        }

        // Create a new packet containing the message
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address);

        // Try to send the message numberOfTries times, each time waiting for an ACK message for millisToWait milliseconds
        for (int i = 1; i <= numberOfTries; i++) {

            // Send the message
            if (verbose) System.out.println("Sending " + message.getType() + " message " + message.getMessageNumber() + " for the " + i + ". time");
            try {
                socket.send(packet);
            } catch (IOException e) {
                System.err.println("Failed to send the " + message.getType() + " message: " + e.getMessage());
                return false;
            }

            // Wait for the ACK message
            AckMessage ackMessage;
            try {
                ackMessage = ackMessageQueue.poll(millisToWait, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                System.err.println("Was rudely interrupted while waiting for the ACK message: " + e.getMessage());
                continue;
            }
            if (ackMessage == null) {
                System.err.println("Have not received an ACK message in 5 seconds");
                continue;
            }

            // Check if the message number is the same as the one sent
            if (ackMessage.getMessageNumber() != message.getMessageNumber()) {
                System.err.println("Received an ACK message with a different message number");
                continue;
            }

            // Check if the UID is the same as the client's
            if (ackMessage.getUid() != uid) {
                System.err.println("Received an ACK message with a different UID");
                continue;
            }

            if (verbose) System.out.println("Received an ACK message for the " + message.getType() + " message " + message.getMessageNumber());

            return true;
        }

        // Maximum number of tries reached
        System.err.println("Have not received an ACK message for the " + message.getType() + " message after 10 tries");
        return false;
    }
}
