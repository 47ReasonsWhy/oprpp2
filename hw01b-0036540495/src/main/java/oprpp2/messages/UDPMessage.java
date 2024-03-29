package oprpp2.messages;

import java.io.IOException;

/**
 * Abstract class modelling all the different UDP messages used by {@link oprpp2.Client} and {@link oprpp2.Server}.
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public abstract class UDPMessage {

    /**
     * Enum representing the type of the message.
     */
    protected final MessageType type;

    /**
     * The message number in the sequence of messages during communication.
     */
    protected long messageNumber;

    /**
     * Constructs a new UDP message with the given type and message number.
     *
     * @param type the type of the message
     * @param messageNumber the message number
     */
    public UDPMessage(MessageType type, long messageNumber) {
        this.type = type;
        this.messageNumber = messageNumber;
    }

    /**
     * Returns the type of the message.
     *
     * @return the type of the message
     */
    public MessageType getType() {
        return type;
    }

    /**
     * Returns the message number.
     *
     * @return the message number
     */
    public long getMessageNumber() {
        return messageNumber;
    }


    /**
     * Serializes the message to a byte array.
     * They can then be deserialized via {@link #deserialize(byte[])}.
     *
     * @return the serialized message
     * @throws IOException if the message cannot be serialized
     */
    public abstract byte[] serialize() throws IOException;

    /**
     * Sets the fields of the message from the data
     * serialized by {@link #serialize()}.
     *
     * @param data the serialized message
     * @throws IOException if the given data cannot be deserialized into a message
     */
    public abstract void deserialize(byte[] data) throws IOException;
}
