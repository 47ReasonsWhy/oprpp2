package hr.fer.zemris.java.custom.scripting.parser;

/**
 * An exception that is thrown when an error occurs while parsing the input text with {@link SmartScriptParser}.
 *
 * @see SmartScriptParser
 * @see RuntimeException
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public class SmartScriptParserException extends RuntimeException {
    /**
     * Constructs a new exception with null as its detail message.
     *
     * @param message the detail message
     */
    public SmartScriptParserException(String message) {
        super(message);
    }
}
