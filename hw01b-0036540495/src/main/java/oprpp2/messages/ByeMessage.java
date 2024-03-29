package oprpp2.messages;

import java.io.*;
import java.util.Objects;

/**
 * Sent by a {@link oprpp2.Client} to the {@link oprpp2.Server} when it wants to disconnect.
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public final class ByeMessage extends UDPMessage {

    /**
     * The unique identifier of the client that is disconnecting.
     */
    private long uid;

    public long getUid() {
        return uid;
    }

    /**
     * Constructs a new {@link ByeMessage} with the given message number and unique identifier.
     *
     * @param messageNumber the message number
     * @param uid the unique identifier
     */
    public ByeMessage(long messageNumber, long uid) {
        super(MessageType.BYE, messageNumber);
        this.uid = uid;
    }

    /**
     * Constructs a new {@link ByeMessage} by deserializing the given data.
     *
     * @param data the data to deserialize
     * @throws IOException if the data cannot be deserialized into a {@link ByeMessage}
     */
    public ByeMessage(byte[] data) throws IOException {
        super(MessageType.BYE, -1);
        deserialize(data);
    }

    @Override
    public MessageType getType() {
        return MessageType.BYE;
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
        var that = (ByeMessage) obj;
        return this.messageNumber == that.messageNumber &&
                this.uid == that.uid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageNumber, uid);
    }

    @Override
    public String toString() {
        return "ByeMessage[" +
                "messageNumber=" + messageNumber + ", " +
                "uid=" + uid + ']';
    }

}
