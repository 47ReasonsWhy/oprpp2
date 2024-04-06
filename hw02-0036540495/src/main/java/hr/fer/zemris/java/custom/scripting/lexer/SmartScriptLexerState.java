package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * Enum that represents all possible lexer states in {@link SmartScriptLexer}.
 *
 * @see SmartScriptLexer
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public enum SmartScriptLexerState {
    /**
     * Everything outside of tags.
     */
    TEXT,

    /**
     * Name of the tag.
     */
    TAG_NAME,

    /**
     * The rest of the tag.
     */
    TAG_DEF
}
