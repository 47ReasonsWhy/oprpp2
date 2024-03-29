package oprpp2.messages;

/**
 * Enum representing the type of {@link UDPMessage}.
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public enum MessageType {
    HELLO(1), ACK(2), BYE(3), OUTMSG(4), INMSG(5);

    public final byte ord;

    MessageType(int ord) {
        this.ord = (byte) ord;
    }

    /**
     * Returns the {@link MessageType} of the given ordinal.
     *
     * @param ord the ordinal
     * @return the {@link MessageType} of the ordinal
     */
    public static MessageType fromOrd(byte ord) {
        for (MessageType type : values()) {
            if (type.ord == ord) {
                return type;
            }
        }
        return null;
    }
}
