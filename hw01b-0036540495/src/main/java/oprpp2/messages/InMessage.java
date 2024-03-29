package oprpp2.messages;

import java.io.*;
import java.util.Objects;

/**
 * Sent by the server to all clients when any client sends a ({@link OutMessage}) message.
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public final class InMessage extends UDPMessage {

    /**
     * The username of the client that sent the message.
     */
    private String username;

    /**
     * The text of the message.
     */
    private String messageText;

    public String getUsername() {
        return username;
    }

    public String getMessageText() {
        return messageText;
    }

    /**
     * Constructs a new {@link InMessage} with the given message number, username and message text.
     *
     * @param messageNumber the message number
     * @param username the username
     * @param messageText the message text
     */
    public InMessage(long messageNumber, String username, String messageText) {
        super(MessageType.INMSG, messageNumber);
        this.username = username;
        this.messageText = messageText;
    }

    /**
     * Constructs a new {@link InMessage} by deserializing the given byte array.
     *
     * @param data the byte array to deserialize
     * @throws IOException if the data cannot be deserialized into an {@link InMessage}
     */
    public InMessage(byte[] data) throws IOException {
        super(MessageType.INMSG, -1);
        deserialize(data);
    }

    @Override
    public MessageType getType() {
        return MessageType.INMSG;
    }

    @Override
    public byte[] serialize() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(bos)) {
            dos.writeByte(getType().ord);
            dos.writeLong(getMessageNumber());
            dos.writeUTF(username);
            dos.writeUTF(messageText);
            return bos.toByteArray();
        }
    }

    @Override
    public void deserialize(byte[] data) throws IOException {
        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data))) {
            dis.readByte();
            messageNumber = dis.readLong();
            username = dis.readUTF();
            messageText = dis.readUTF();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (InMessage) obj;
        return this.messageNumber == that.messageNumber &&
                Objects.equals(this.username, that.username) &&
                Objects.equals(this.messageText, that.messageText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageNumber, username, messageText);
    }

    @Override
    public String toString() {
        return "InMessage[" +
                "messageNumber=" + messageNumber + ", " +
                "username=" + username + ", " +
                "messageText=" + messageText + ']';
    }

}
