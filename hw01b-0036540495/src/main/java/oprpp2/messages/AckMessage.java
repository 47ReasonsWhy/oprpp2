package oprpp2.messages;

import java.io.*;
import java.util.Objects;

/**
 * Used by both {@link oprpp2.Client} and {@link oprpp2.Server} to ACKnowledge the receipt of a message.
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public final class AckMessage extends UDPMessage {

    /**
     * The unique identifier of the client that sent the message or that the message is intended for.
     */
    private long uid;

    public long getUid() {
        return uid;
    }

    /**
     * Constructs a new {@link AckMessage} with the given message number and UID.
     *
     * @param messageNumber the message number
     * @param uid the UID
     */
    public AckMessage(long messageNumber, long uid) {
        super(MessageType.ACK, messageNumber);
        this.uid = uid;
    }

    /**
     * Constructs a new {@link AckMessage} by deserializing the given data.
     *
     * @param data the data to deserialize
     * @throws IOException if the data cannot be deserialized into an {@link AckMessage}
     */
    public AckMessage(byte[] data) throws IOException {
        super(MessageType.ACK, -1);
        deserialize(data);
    }

    @Override
    public byte[] serialize() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(bos)) {
            dos.writeByte(getType().ord);
            dos.writeLong(getMessageNumber());
            dos.writeLong(uid);
            return bos.toByteArray();
        }
    }

    @Override
    public void deserialize(byte[] data) throws IOException {
        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data))) {
            dis.readByte();
            messageNumber = dis.readLong();
            uid = dis.readLong();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AckMessage) obj;
        return this.messageNumber == that.messageNumber &&
                this.uid == that.uid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageNumber, uid);
    }

    @Override
    public String toString() {
        return "AckMessage[" +
                "messageNumber=" + messageNumber + ", " +
                "uid=" + uid + ']';
    }

}
