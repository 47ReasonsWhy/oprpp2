package oprpp2;

import oprpp2.messages.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.*;
import java.security.SecureRandom;
import java.util.concurrent.LinkedBlockingQueue;

import static oprpp2.Util.sendAndWaitForAck;

/**
 * A client for the {@link Server}'s chatroom application.
 * The client connects to the server and communicates with it via UDP,
 * sends and receives messages and displays them in a simple GUI.
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public class Client {
    /**
     * The address of the server.
     */
    private SocketAddress serverAddress;

    /**
     * The username of the client.
     */
    private String username;

    /**
     * A flag indicating whether the client should be printing verbose output.
     */
    private boolean verbose = false;

    /**
     * The number of tries to send a message before giving up.
     */
    private final int numberOfTries = 10;

    /**
     * The number of milliseconds to wait for an ACK message.
     */
    private final long millisToWait = 5000;

    /**
     * The socket used for communication with the server.
     */
    private DatagramSocket socket;

    /**
     * The unique identifier of the client.
     */
    private long uid;

    /**
     * The GUI components.
     */
    private JTextArea messageDisplay;
    private JTextField inputField;

    /**
     * The queue of ACK messages.
     */
    private final LinkedBlockingQueue<AckMessage> ackMessageQueue = new LinkedBlockingQueue<>();

    /**
     * The current message number in the sequence of messages during communication.
     * The {@link MessageType#HELLO} message is sent with message number 0.
     */
    private long messageNumber = 1;


    /**
     * The starting point of the client application.
     * It parses the command line arguments, connects to the server,
     * initializes the GUI and starts the message receiver.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // Instantiate the client
        Client client = new Client();

        // Parse the command line arguments
        if (args.length < 3 || args.length > 4) {
            System.err.println("Invalid number of arguments");
            System.err.println("Usage: java Client <hostName> <hostPort> <username> [--verbose]");
            System.exit(1);
        }
        String hostName = args[0];
        int hostPort;
        try {
            hostPort = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Failed to parse the port number: " + e.getMessage());
            System.err.println("Usage: java Client <hostName> <hostPort> <username> [--verbose]");
            System.exit(1);
            return;
        }
        try {
            client.serverAddress = new InetSocketAddress(InetAddress.getByName(hostName), hostPort);
        } catch (UnknownHostException e) {
            System.err.println("Failed to create a new socket address: " + e.getMessage());
            System.exit(1);
        }
        client.username = args[2];
        if (args.length == 4) {
            if (args[3].equals("--verbose")) {
                client.verbose = true;
            } else {
                System.err.println("Unrecognized argument: " + args[3]);
                System.err.println("Usage: java Client <hostName> <hostPort> <username> [--verbose]");
                System.exit(1);
            }
        }

        // Initialize the socket
        try {
            client.socket = new DatagramSocket();
        } catch (SocketException e) {
            System.err.println("Failed to create a new socket: " + e.getMessage());
            System.exit(1);
        }

        // Connect to the server
        client.uid = client.connect();
        if (client.uid == -1) {
            System.err.println("Failed to connect to the server");
            System.exit(1);
        }

        // Initialize the GUI
        client.initGUI();

        // Start the message receiver
        client.startReceiver();
    }

    /**
     * Initializes the GUI of the client.
     * Sets the layout, adds the message display and input field,
     * and instantiates appropriate listeners.
     */
    private void initGUI() {
        JFrame frame = new JFrame(username + "'s chat with " + serverAddress.toString());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ByeMessage byeMessage = new ByeMessage(messageNumber++, uid);

                // Send the BYE message
                boolean success = sendAndWaitForAck(byeMessage, serverAddress, uid, socket, numberOfTries, millisToWait, ackMessageQueue, verbose);
                if (success) {
                    if (verbose) System.out.println("BYE message confirmed by the server. Closing the socket nicely...");
                } else {
                    System.err.println("Failed to close the connection nicely. Closing the socket forcefully...");
                }

                // Close the socket
                socket.close();
                if (verbose) System.out.println("Socket closed. Exiting the chat. Goodbye!");
            }
        });

        JPanel cp = (JPanel) frame.getContentPane();
        cp.setLayout(new BorderLayout());
        cp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Message display
        messageDisplay = new JTextArea();
        messageDisplay.setEditable(false);
        messageDisplay.setBorder(BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cp.add(new JScrollPane(messageDisplay), BorderLayout.CENTER);

        // Message input field container, so it looks nice :)
        JPanel inputFieldContainer = new JPanel();
        inputFieldContainer.setLayout(new BorderLayout());
        inputFieldContainer.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Message input field
        inputField = new JTextField("Type your message here...");
        inputField.setForeground(Color.GRAY);
        inputField.setEditable(true);
        inputField.setBorder(BorderFactory.createEmptyBorder(1, 3, 1, 3));

        inputFieldContainer.add(inputField, BorderLayout.CENTER);
        cp.add(inputFieldContainer, BorderLayout.SOUTH);

        // Set the focus to the input field
        inputField.requestFocusInWindow();

        // Initiate appropriate listeners on the input field
        inputField.addActionListener(e -> {
            String message = inputField.getText();

            // Disable the input field
            inputField.setEditable(false);
            inputField.setForeground(Color.GRAY);
            inputField.setText("Sending message. Please wait...");

            // Create and send the OUTMSG message
            OutMessage outMessage = new OutMessage(messageNumber++, uid, message);
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    return sendAndWaitForAck(outMessage, serverAddress, uid, socket, numberOfTries, millisToWait, ackMessageQueue, verbose);
                }

                @Override
                protected void done() {
                    boolean failed = true;
                    try {
                        if (get()) {
                            failed = false;
                            inputField.setText("");
                            inputField.setForeground(Color.BLACK);
                            inputField.setEditable(true);
                        }
                    } catch (Exception ignored) {
                    }
                    if (failed) {
                        socket.close();
                        if (verbose) System.out.println("Shutting down the client. Socket closed. Exiting the chat. Goodbye!");
                        frame.dispose();
                    }
                }
            }.execute();
        });

        inputField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (inputField.getForeground().equals(Color.GRAY)) {
                    inputField.setText("");
                    inputField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (inputField.getText().isEmpty()) {
                    inputField.setForeground(Color.GRAY);
                    inputField.setText("Type your message here...");
                }
            }
        });

        // Set the frame properties
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Initializes the connection to the server.
     * Sends the HELLO message to the server and waits for the ACK message.
        *
     * @return the unique identifier of the client if the connection was successful, -1 otherwise
     */
    private long connect() {

        // Generate a new random key
        SecureRandom random = new SecureRandom();
        long randkey = random.nextLong();

        // Construct a new HELLO message
        HelloMessage helloMessage = new HelloMessage(0, username, randkey);

        // Create a new packet containing the HELLO message
        byte[] buffer;
        try {
            buffer = helloMessage.serialize();
        } catch (IOException e) {
            System.err.println("Failed to serialize the HELLO message: " + e.getMessage());
            return -1;
        }
        DatagramPacket sPacket;
        sPacket = new DatagramPacket(buffer, buffer.length, serverAddress);

        // Set the timeout to 5 seconds and timesNotReceived counter to 0
        try {
            socket.setSoTimeout(5000);
        } catch (SocketException e) {
            System.err.println("Failed to set the socket timeout: " + e.getMessage());
            return -1;
        }

        // Send the HELLO message
        for (int i = 1; i <= 10; i++) {
            if (verbose) System.out.println("Sending HELLO message to " + serverAddress.toString() + " for the " + i + ". time");
            try {
                socket.send(sPacket);
            } catch (IOException e) {
                System.err.println("Failed to send the HELLO message: " + e.getMessage());
                return -1;
            }

            // Receive the ACK message
            buffer = new byte[1024];
            DatagramPacket rPacket = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(rPacket);
            } catch (SocketTimeoutException e) {
                System.err.println("Have not received an ACK message in 5 seconds");
                continue;
            } catch (IOException e) {
                System.err.println("An IO error occurred while receiving the ACK message: " + e.getMessage());
                System.err.println("Giving up.");
                return -1;
            }

            // Check the type of the received message
            MessageType type = MessageType.fromOrd(buffer[0]);
            if (type != MessageType.ACK) {
                System.err.println("First byte of the received message does not indicate an ACK message");
                continue;
            }

            // Deserialize the ACK message
            AckMessage ackMessage;
            try {
                ackMessage = new AckMessage(buffer);
            } catch (IOException e) {
                System.err.println("Failed to deserialize the ACK message: " + e.getMessage());
                continue;
            }

            // Check if the message is meant as a response to HELLO
            if (ackMessage.getMessageNumber() != 0) {
                System.err.println("Received an ACK message with a different message number");
                continue;
            }

            // Print the received message
            if (verbose)
                System.out.println("Received ACK message with UID " + ackMessage.getUid() + "\n"
                                 + "Connection established with " + serverAddress + " as " + username + "\n"
                                 + "Starting the chat...\n");

            // Return the UID
            return ackMessage.getUid();
        }

        // Maximum number of tries reached
        System.err.println("Failed to connect to the server after 10 tries");
        return -1;
    }

    /**
     * Starts the message receiver.
     * The receiver listens for incoming messages and processes them accordingly:
     * <ul>
     * <li>ACK messages are added to the queue</li>
     * <li>INMSG messages are printed and shown in the GUI, and appropriate ACK messages are sent as a response</li>
     */
    private void startReceiver() {
        new Thread(() -> {
            // Create a new packet
            byte[] buffer = new byte[1024];
            DatagramPacket rPacket = new DatagramPacket(buffer, buffer.length);

            // Listen for incoming messages
            while (true) {
                try {
                    socket.setSoTimeout(0);
                    socket.receive(rPacket);
                } catch (IOException e) {
                    if (socket.isClosed()) {
                        if (verbose) System.out.println("Socket closed, stopping the receiver nicely");
                        return;
                    }
                    System.err.println("An IO error occurred while receiving the message: " + e.getMessage());
                    return;
                }

                // Check the type of the received message
                MessageType type = MessageType.fromOrd(buffer[0]);
                if (type == null) {
                    System.err.println("Received a message of an unknown type: " + buffer[0]);
                    continue;
                }
                switch (type) {
                    case ACK -> {
                        // Deserialize the ACK message
                        AckMessage ackMessage;
                        try {
                            ackMessage = new AckMessage(buffer);
                        } catch (IOException e) {
                            System.err.println("Failed to deserialize the ACK message: " + e.getMessage());
                            continue;
                        }

                        // Check if the message is meant for this client
                        if (ackMessage.getUid() != uid) {
                            System.err.println("Received an ACK message with a different UID. Throwing it into the trash...");
                            continue;
                        }

                        // Add the ACK message to the queue
                        if (verbose) System.out.println("Received ACK message number " + ackMessage.getMessageNumber());
                        ackMessageQueue.add(ackMessage);
                    }
                    case INMSG -> {
                        // Deserialize the INMSG message
                        InMessage inMessage;
                        try {
                            inMessage = new InMessage(buffer);
                        } catch (IOException e) {
                            System.err.println("Failed to deserialize the INMSG message: " + e.getMessage());
                            continue;
                        }

                        // Print the received message and show it in the GUI
                        if (verbose) System.out.println("Received INMSG message from " + inMessage.getUsername() + ": " + inMessage.getMessageText());
                        SwingUtilities.invokeLater(() -> messageDisplay.append(inMessage.getMessageText() + "\n"));

                        // Create and send the appropriate ACK message
                        AckMessage ackMessage = new AckMessage(inMessage.getMessageNumber(), uid);
                        byte[] data;
                        try {
                            data = ackMessage.serialize();
                        } catch (IOException e) {
                            System.err.println("Failed to serialize the ACK message: " + e.getMessage());
                            return;
                        }
                        DatagramPacket packet = new DatagramPacket(data, data.length, rPacket.getAddress(), rPacket.getPort());
                        try {
                            socket.send(packet);
                        } catch (IOException e) {
                            System.err.println("Failed to send the ACK message: " + e.getMessage());
                            return;
                        }
                    }
                    default -> System.err.println("Received a message of an unsupported type: " + type);
                }
            }
        }).start();
    }
}
