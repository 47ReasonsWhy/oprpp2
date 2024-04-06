package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * Enum that represents all possible token types in {@link SmartScriptLexer}.
 *
 * @see SmartScriptLexer
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public enum SmartScriptTokenType {
    /**
     * Any text that is not in tags.
     */
    TEXT,

    /**
     * Number-dot-number.
     */
    DOUBLE,

    /**
     * Number without dot.
     */
    INTEGER,

    /**
     * String (text inside double quotes).
     */
    STRING,

    /**
     * Variable name
     * <p>
     * Starts with a letter, and after that can contain letters, digits and underscores.
     */
    VARIABLE,

    /**
     * Function name.
     * <p>
     * Starts with '@', then a letter, and after that can contain letters, digits and underscores.
     */
    FUNCTION,

    /**
     * Operator.
     * <p>
     * Can be '+', '-', '*', '/', '^'.
     */
    OPERATOR,

    /**
     * "{$"
     */
    START_TAG_DEF,

    /**
     * "$}"
     */
    END_TAG_DEF,


    /**
     * Tag name.
     * <p>
     * Always comes after {@link SmartScriptTokenType#START_TAG_DEF}
     */
    TAG_NAME,

    /**
     * End of file.
     */
    EOF,
}
