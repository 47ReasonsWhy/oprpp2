package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * A class that represents a {@link RuntimeException} thrown by {@link SmartScriptLexer}.
 *
 * @see SmartScriptLexer
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public class SmartScriptLexerException extends RuntimeException {

    /**
     * Constructs a {@link SmartScriptLexerException} with a given message.
     *
     * @param message the detail message
     */
    public SmartScriptLexerException(String message) {
        super(message);
    }
}
