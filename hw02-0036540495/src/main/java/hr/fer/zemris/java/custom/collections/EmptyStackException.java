package hr.fer.zemris.java.custom.collections;

/**
 * Exception thrown when the stack is empty.
 *
 * @see ObjectStack
 *
 * @version 1.1
 * @author Marko Šelendić
 */
public class EmptyStackException extends RuntimeException {
    /**
     * Creates an instance of EmptyStackException.
     *
     * @param message message of the exception
     */
    public EmptyStackException(String message) {
            super(message);
        }
}
