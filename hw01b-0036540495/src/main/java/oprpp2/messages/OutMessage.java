package oprpp2.messages;

import java.io.*;
import java.util.Objects;

/**
 * Sent by the {@link oprpp2.Client} to the {@link oprpp2.Server} when a user sends a new message to the chat.
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public final class OutMessage extends UDPMessage {

    /**
     * The unique identifier of the user that sent the message.
     */
    private long uid;

    /**
     * The text of the message.
     */
    private String messageText;

    public long getUid() {
        return uid;
    }

    public String getMessageText() {
        return messageText;
    }

    /**
     * Constructs a new {@link OutMessage} with the given message number, user identifier, and message text.
     *
     * @param messageNumber the message number
     * @param uid the user identifier
     * @param messageText the message text
     */
    public OutMessage(long messageNumber, long uid, String messageText) {
        super(MessageType.OUTMSG, messageNumber);
        this.uid = uid;
        this.messageText = messageText;
    }

    /**
     * Constructs a new {@link OutMessage} by deserializing the given data.
     *
     * @param data the data to deserialize
     * @throws IOException if the data cannot be deserialized into an {@link OutMessage}
     */
    public OutMessage(byte[] data) throws IOException {
        super(MessageType.OUTMSG, -1);
        deserialize(data);
    }

    @Override
    public MessageType getType() {
        return MessageType.OUTMSG;
    }

    @Override
    public byte[] serialize() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(bos)) {
            dos.writeByte(getType().ord);
            dos.writeLong(getMessageNumber());
            dos.writeLong(uid);
            dos.writeUTF(messageText);
            return bos.toByteArray();
        }
    }

    @Override
    public void deserialize(byte[] data) throws IOException {
        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data))) {
            dis.readByte();
            messageNumber = dis.readLong();
            uid = dis.readLong();
            messageText = dis.readUTF();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (OutMessage) obj;
        return this.messageNumber == that.messageNumber &&
                this.uid == that.uid &&
                Objects.equals(this.messageText, that.messageText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageNumber, uid, messageText);
    }

    @Override
    public String toString() {
        return "OutMessage[" +
                "messageNumber=" + messageNumber + ", " +
                "uid=" + uid + ", " +
                "messageText=" + messageText + ']';
    }

}
