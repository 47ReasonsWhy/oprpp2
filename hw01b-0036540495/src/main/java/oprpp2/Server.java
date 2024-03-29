package oprpp2;

import oprpp2.messages.*;

import java.io.IOException;
import java.net.*;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;

import static oprpp2.Util.sendAndWaitForAck;

/**
 * A UDP server that listens for messages from clients and forwards them to all, modelling a simple chatroom.
 *
 * @see UDPMessage
 * @see Client
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public class Server {

    /**
     * The port on which the server listens for messages.
     */
    private int port;

    /**
     * A flag indicating whether the server should be printing verbose output.
     */
    private boolean verbose;

    /**
     * The number of tries to send a message before giving up.
     */
    private final int numberOfTries = 10;

    /**
     * The number of milliseconds to wait for the ACK message.
     */
    private final long millisToWait = 5000;

    /**
     * The socket used for communication with clients.
     */
    private DatagramSocket socket;

    /**
     * The user identifier of the next client that connects to the server.
     */
    private long uid = new SecureRandom().nextLong();

    /**
     * The set of all the connections established with clients.
     */
    private final HashSet<Connection> connections = new HashSet<>();


    /**
     * The starting point of the server application.
     * It parses the command line arguments, initializes the socket and starts the {@link #serve()} method.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Instantiate the server
        Server server = new Server();

        // Parse the command line arguments
        if (args.length < 1 || args.length > 2) {
            System.err.println("Usage: java Server <port> [--verbose]");
            System.exit(1);
        }
        try {
            server.port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Port must be an integer.");
            System.exit(1);
        }
        if (args.length == 2) {
            if (args[1].equals("--verbose")) {
                server.verbose = true;
            } else {
                System.err.println("Unrecognized argument: " + args[1]);
                System.err.println("Usage: java Server <port> [--verbose]");
                System.exit(1);
            }
        }

        // Initialize the socket
        try {
            server.socket = new DatagramSocket(new InetSocketAddress(server.port));
        } catch (SocketException e) {
            System.err.println("Failed to create a socket on port " + server.port);
            System.exit(1);
        }

        // Start the server
        System.out.println("Server started on port " + server.port + ". Listening for packets...");
        server.serve();
    }

    /**
     * The main loop of the server that listens for packets and handles them accordingly.
     */
    private void serve() {
        while (true) {
            // Receive a packet
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                System.err.println("IO error while receiving a packet: " + e.getMessage());
                continue;
            }

            // Deserialize the packet
            MessageType messageType = MessageType.fromOrd(buffer[0]);
            if (messageType == null) {
                System.err.println("Received a message of unknown type: " + buffer[0]);
                continue;
            }
            switch (messageType) {
                case HELLO -> handleHello(buffer, packet);
                case ACK -> handleAck(buffer);
                case BYE -> handleBye(buffer);
                case OUTMSG -> handleOutMsg(buffer, packet);
                default -> System.err.println("Received a message of invalid type: " + messageType);
            }
        }

        // Should never reach this point
    }

    /**
     * Handles a {@link MessageType#HELLO} message.
     * Establishes a connection with the client and sends an ACK message.
     *
     * @param buffer the buffer containing the message
     * @param packet the packet containing the message
     */
    private void handleHello(byte[] buffer, DatagramPacket packet) {
        // Deserialize the message
        HelloMessage message;
        try {
            message = new HelloMessage(buffer);
        } catch (IOException e) {
            System.err.println("Failed to deserialize a HELLO message: " + e.getMessage());
            return;
        }
        if (verbose) System.out.println("Received HELLO message from " + packet.getSocketAddress());

        // Establish a connection if the client is new
        Connection connection = connections.stream()
                .filter(c -> c.address.equals(packet.getSocketAddress()) && c.randkey == message.getRandkey())
                .findFirst()
                .orElse(null);
        long uidForAck = connection == null ? uid : connection.uid;
        if (connection != null) {
            if (verbose) System.out.println("Connection already established with " + packet.getSocketAddress());
        } else {
            connection = new Connection(packet.getSocketAddress(), message.getRandkey(), message.getUsername(), uid++);
            connections.add(connection);
            if (verbose) System.out.println("Established connection with " + packet.getSocketAddress());
        }

        // In any case, send an appropriate ACK message
        AckMessage ack = new AckMessage(0, uidForAck);
        try {
            buffer = ack.serialize();
        } catch (IOException e) {
            System.err.println("Failed to serialize an ACK message: " + e.getMessage());
            return;
        }
        packet.setData(buffer);
        packet.setLength(buffer.length);
        try {
            socket.send(packet);
        } catch (IOException e) {
            System.err.println("Failed to send an ACK to HELLO: " + e.getMessage());
            return;
        }
        if (verbose) System.out.println("Sent an ACK message to HELLO from " + packet.getSocketAddress() + "\n"
                + "Delegating the connection to a new thread...");

        // Start the connection handler for forwarding OUTMSG/INMSG messages
        connection.handler.start();
    }

    /**
     * Handles an {@link MessageType#ACK} message.
     * Adds the message to the appropriate connection's queue.
     *
     * @param buffer the buffer containing the message
     */
    private void handleAck(byte[] buffer) {
        // Deserialize the message
        AckMessage message;
        try {
            message = new AckMessage(buffer);
        } catch (IOException e) {
            System.err.println("Failed to deserialize an ACK message: " + e.getMessage());
            return;
        }
        if (verbose) System.out.println("Received ACK message from " + message.getUid());

        // Find the connection
        Connection connection = connections.stream()
                .filter(c -> c.uid == message.getUid())
                .findFirst()
                .orElse(null);
        if (connection == null) {
            System.err.println("Received an ACK message from an unknown connection: " + message.getUid());
            return;
        }

        // Add the ACK to the connection's queue
        connection.acks.add(message);

        if (verbose) System.out.println("Added " + message.getMessageNumber() + ". ACK message to the queue of " + message.getUid());
    }

    /**
     * Handles a {@link MessageType#BYE} message accordingly.
     *
     * @param buffer the buffer containing the message
     */
    private void handleBye(byte[] buffer) {
        // Deserialize the message
        ByeMessage message;
        try {
            message = new ByeMessage(buffer);
        } catch (IOException e) {
            System.err.println("Failed to deserialize a BYE message: " + e.getMessage());
            return;
        }
        if (verbose) System.out.println("Received BYE message from " + message.getUid());

        // Find the connection
        Connection connection = connections.stream()
                .filter(c -> c.uid == message.getUid())
                .findFirst()
                .orElse(null);
        if (connection == null) {
            System.err.println("Received a BYE message from an unknown connection: " + message.getUid());
            return;
        }

        // Check the message number
        if (message.getMessageNumber() > connection.nextOut) {
            // If it is larger however, OUTMSG messages were lost, so we need to wait for them (throw this message away)
            if (verbose) System.err.println("Received a BYE message with a larger message number than expected: " + message.getMessageNumber() + " > " + connection.nextOut);
            return;
        } else if (message.getMessageNumber() == connection.nextOut) {
            // If it is equal, we can remove the connection
            connection.handler.interrupt();
            connections.remove(connection);
            if (verbose) System.out.println("Removing connection with " + message.getUid());
        }
        if (message.getMessageNumber() < connection.nextOut && verbose) {
            // If it is equal or smaller, some ACK messages were lost
            System.out.println("Received a BYE message with a smaller message number than expected: " + message.getMessageNumber() + " < " + connection.nextOut);
        }

        // We need to send an ACK message if the message number is equal or smaller
        try {
            sendAnAckTo(message, connection.address);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Handles an {@link MessageType#OUTMSG} message.
     * Adds the message to all the connections' queues and sends an ACK message.
     *
     * @param buffer the buffer containing the message
     * @param packet the packet containing the message
     */
    private void handleOutMsg(byte[] buffer, DatagramPacket packet) {
        // Deserialize the message
        OutMessage message;
        try {
            message = new OutMessage(buffer);
        } catch (IOException e) {
            System.err.println("Failed to deserialize an OUTMSG message: " + e.getMessage());
            return;
        }
        if (verbose) System.out.println("Received OUTMSG message from " + packet.getSocketAddress() + "\n"
                + "Adding it to all the connections' queues...");

        // Check the OUTMSG number and compare it to the next expected OUTMSG message number of the connection
        Connection connection = connections.stream()
                .filter(c -> c.uid == message.getUid())
                .findFirst()
                .orElse(null);
        if (connection == null) {
            System.err.println("Received an OUTMSG message from an unknown connection: " + packet.getSocketAddress());
            return;
        }
        // If it is larger, OUTMSG messages were lost, so we need to wait for them (throw this message away)
        if (message.getMessageNumber() > connection.nextOut) {
            if (verbose) System.err.println("Received an OUTMSG message with a larger message number than expected: " + message.getMessageNumber() + " > " + connection.nextOut);
            return;
        }
        if (message.getMessageNumber() == connection.nextOut) {
            // If it is equal, add a datetime stamp and other info to the message, ...
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            String newText = String.format("[%s %s]: %s",
                    LocalDateTime.now().format(formatter),
                    connection.username,
                    message.getMessageText()
            );
            OutMessage newMessage = new OutMessage(message.getMessageNumber(), message.getUid(), newText);

            // ...add the message to all the connection's queues,
            // increment the appropriate message number and send the ACK message
            connections.forEach(c -> c.outs.add(newMessage));
            connection.nextOut++;
        }
        // If the uid is equal or smaller, ACK messages were lost, so we need to resend them
        if (verbose) System.out.println("Received an OUTMSG message with a smaller message number than expected: " + message.getMessageNumber() + " < " + connection.nextOut);
        try {
            sendAnAckTo(message, connection.address);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Sends an ACK message to the client that sent the given message.
     *
     * @param message the message to which to send an ACK
     * @param address the address of the client
     * @throws IOException if the ACK message could not be serialized or sent
     */
    private void sendAnAckTo(UDPMessage message, SocketAddress address) throws IOException {
        long uid = switch (message.getType()) {
            case BYE -> ((ByeMessage) message).getUid();
            case OUTMSG -> ((OutMessage) message).getUid();
            default -> throw new IllegalArgumentException();
        };
        AckMessage ack = new AckMessage(message.getMessageNumber(), uid);
        byte[] buffer;
        try {
            buffer = ack.serialize();
        } catch (IOException e) {
            throw new IOException("Failed to serialize an " + message.getType().toString() +  " message: " + e.getMessage());
        }
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address);
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new IOException("Failed to send an ACK to " + message.getType().toString() +  ": " + e.getMessage());
        }
        if (verbose) System.out.println("Sent an ACK " + message.getMessageNumber() + " message to " + message.getType().toString() +  " from " + uid);
    }

    /**
     * A class representing a connection between the server and a client.
     */
    private class Connection {

        /**
         * The address of the client.
         */
        private final SocketAddress address;

        /**
         * The client's random key used during the initial handshake.
         */
        private final long randkey;

        /**
         * The client's username.
         */
        private final String username;

        /**
         * The unique identifier of the client.
         */
        private final long uid;

        /**
         * The queue of ACK messages received from the client.
         */
        private final LinkedBlockingQueue<AckMessage> acks;

        /**
         * The queue of OUTMSG messages received from the client.
         */
        private final LinkedBlockingQueue<OutMessage> outs;

        /**
         * The next expected OUTMSG message number.
         */
        private long nextOut;

        /**
         * The next expected INMSG message number.
         */
        private long nextIn;

        /**
         * The thread that handles the forwarding of this connection's INMSG messages.
         */
        private final Thread handler;

        /**
         * Constructs a new connection with the given address, random key, username and unique identifier.
         *
         * @param address client's address
         * @param randkey client's random key
         * @param username client's username
         * @param uid client's unique identifier
         */
        public Connection(SocketAddress address, long randkey, String username, long uid) {
            this.address = address;
            this.randkey = randkey;
            this.username = username;
            this.uid = uid;
            this.acks = new LinkedBlockingQueue<>();
            this.outs = new LinkedBlockingQueue<>();
            this.nextOut = 1;
            this.nextIn = 1;
            this.handler = new Thread(() -> Server.this.forwardOuts(this));
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Connection other) {
                return address.equals(other.address) && randkey == other.randkey;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return address.hashCode() ^ Long.hashCode(randkey);
        }
    }

    /**
     * Used by {@link Connection#handler}.
     * Waits for a new OUTMSG message in {@link Connection#outs} queue,
     * transforms it into an INMSG message and forwards it to its client.
     *
     * @param connection the connection to forward messages for
     */
    private void forwardOuts(Connection connection) {
        while (true) {
            // Wait for a message to send
            OutMessage outMessage;
            try {
                outMessage = connection.outs.take();
            } catch (InterruptedException e) {
                if (verbose) System.out.println("Was nicely interrupted. Sad to see you go " + connection.username + " :( ...");
                continue;
            }
            if (verbose) System.out.println("Forwarding OUTMSG message " + outMessage.getMessageNumber() + " to " + connection.address);

            // Find the username of the sender
            String username = connections.stream()
                    .filter(c -> c.uid == outMessage.getUid())
                    .findFirst()
                    .map(c -> c.username)
                    .orElse(null);
            if (username == null) {
                System.err.println("Failed to find the username of the sender of the OUTMSG message " + outMessage.getMessageNumber());
                continue;
            }

            // Create a new INMSG message
            InMessage message = new InMessage(connection.nextIn++, username, outMessage.getMessageText());

            // Send it and wait for an ACK
            boolean success = sendAndWaitForAck(message, connection.address, connection.uid, socket, numberOfTries, millisToWait, connection.acks, verbose);

            // If it failed 10 times, turn off this connection
            if (!success) {
                connections.remove(connection);
                if (verbose) System.out.println("Removing connection with " + connection.uid);
                break;
            }
        }
    }
}
