package oprpp2.messages;

import java.io.*;
import java.util.Objects;

/**
 * Sent by the {@link oprpp2.Client} to the {@link oprpp2.Server} to announce that it wants to connect (join the chat).
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public final class HelloMessage extends UDPMessage {

    /**
     * The username of the client that is connecting.
     */
    private String username;

    /**
     * A random key that the client generates and sends to the server.
     */
    private long randkey;

    public String getUsername() {
        return username;
    }

    public long getRandkey() {
        return randkey;
    }

    /**
     * Constructs a new {@link HelloMessage} with the given message number, username, and random key.
     *
     * @param messageNumber the message number
     * @param username the username
     * @param randkey the random key
     */
    public HelloMessage(long messageNumber, String username, long randkey) {
        super(MessageType.HELLO, messageNumber);
        this.username = username;
        this.randkey = randkey;
    }

    /**
     * Constructs a new {@link HelloMessage} by deserializing the given data.
     *
     * @param data the data to deserialize
     * @throws IOException if the data cannot be deserialized into a {@link HelloMessage}
     */
    public HelloMessage(byte[] data) throws IOException {
        super(MessageType.HELLO, -1);
        deserialize(data);
    }

    @Override
    public MessageType getType() {
        return MessageType.HELLO;
    }

    @Override
    public byte[] serialize() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(bos)) {
            // Write the messageText type, messageText number, username (as UTF-8, prefixed with length), and random key
            dos.writeByte(getType().ord);
            dos.writeLong(messageNumber);
            dos.writeUTF(username);
            dos.writeLong(randkey);
            return bos.toByteArray();
        }
    }

    @Override
    public void deserialize(byte[] data) throws IOException {
        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data))) {
            dis.readByte();
            messageNumber = dis.readLong();
            username = dis.readUTF();
            randkey = dis.readLong();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (HelloMessage) obj;
        return this.messageNumber == that.messageNumber &&
                Objects.equals(this.username, that.username) &&
                this.randkey == that.randkey;
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageNumber, username, randkey);
    }

    @Override
    public String toString() {
        return "HelloMessage[" +
                "messageNumber=" + messageNumber + ", " +
                "username=" + username + ", " +
                "randkey=" + randkey + ']';
    }

}
