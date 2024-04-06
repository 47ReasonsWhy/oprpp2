package hr.fer.zemris.java.custom.scripting.lexer;

import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

/**
 * Lexer for {@link SmartScriptParser}
 *
 * @see SmartScriptLexerException
 * @see SmartScriptLexerState
 * @see SmartScriptParser
 * @see SmartScriptToken
 * @see SmartScriptTokenType
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public class SmartScriptLexer {
    /**
     * Input text.
     */
    private final char[] data;

    /**
     * Current token.
     */
    private SmartScriptToken token;

    /**
     * Index of the first unprocessed character.
     */
    private int currentIndex;

    /**
     * Current state of the lexer.
     */
    private SmartScriptLexerState state;

    /**
     * Creates a new lexer with the given text as input.
     *
     * @param text input text
     * @throws NullPointerException if the given text is null
     */
    public SmartScriptLexer(String text) {
        if (text == null) {
            throw new NullPointerException("Input text cannot be null.");
        }
        this.data = text.toCharArray();
        this.token = null;
        this.currentIndex = 0;
        this.state = SmartScriptLexerState.TEXT;
    }

    /**
     * Returns the last token that was generated.
     * Will not generate the next token even if called multiple times.
     *
     * @return last generated token
     */
    public SmartScriptToken getToken() {
        return token;
    }

    /**
     * Returns the current state of the lexer.
     *
     * @return current state
     */
    public SmartScriptLexerState getState() {
        return state;
    }

    /**
     * Returns the index of the first unprocessed character.
     *
     * @return index of the first unprocessed character
     */
    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * Sets the state of the lexer.
     *
     * @param state new state
     * @throws NullPointerException if the given state is null
     */
    public void setState(SmartScriptLexerState state) {
        if (state == null) {
            throw new NullPointerException("Lexer state cannot be null.");
        }
        this.state = state;
    }

    /**
     * Generates and returns the next token.
     *
     * @return next token
     * @throws SmartScriptLexerException if an error occurs
     */
    public SmartScriptToken nextToken() throws SmartScriptLexerException {
        // If we already generated the EOF token, there should be no more tokens available.
        if (token != null && token.type() == SmartScriptTokenType.EOF) {
            throw new SmartScriptLexerException("No more tokens available.");
        }

        // If we reached the end of the input, generate the EOF token.
        if (currentIndex >= data.length) {
            token = new SmartScriptToken(SmartScriptTokenType.EOF, null);
            return token;
        }

        // Else, generate the next token, based on the current state of the lexer.
        return switch (state) {
            case TEXT -> textState();
            case TAG_NAME -> tagNameState();
            case TAG_DEF -> tagDefState();
        };
    }

    /**
     * Generates the next token in the TEXT state.
     *
     * @return next token
     * @throws SmartScriptLexerException if an error occurs
     */
    private SmartScriptToken textState() throws SmartScriptLexerException {
        // If the next character is an opening tag, generate the opening tag token.
        if (data[currentIndex] == '{' && currentIndex + 1 < data.length && data[currentIndex + 1] == '$') {
            token = new SmartScriptToken(SmartScriptTokenType.START_TAG_DEF, null);
            currentIndex += 2;
            return token;
        }

        // Else, generate the text token.
        StringBuilder sb = new StringBuilder();

        while (currentIndex < data.length) {
            if (data[currentIndex] == '{' && currentIndex + 1 < data.length && data[currentIndex + 1] == '$') {
                break;
            }

            if (data[currentIndex] == '\\') {
                if (currentIndex + 1 < data.length && (data[currentIndex + 1] == '\\' || data[currentIndex + 1] == '{')) {
                    sb.append(data[currentIndex + 1]);
                    currentIndex += 2;
                    continue;
                } else {
                    throw new SmartScriptLexerException("Invalid escape sequence at index " + currentIndex + ".");
                }
            }

            sb.append(data[currentIndex]);
            currentIndex++;
        }

        token = new SmartScriptToken(SmartScriptTokenType.TEXT, sb.toString());
        return token;
    }

    /**
     * Generates the next token in the TAG_NAME state.
     *
     * @return next token
     * @throws SmartScriptLexerException if an error occurs
     */
    private SmartScriptToken tagNameState() throws SmartScriptLexerException {
        // The only option is to generate a TAG_NAME token.
        StringBuilder sb = new StringBuilder();

        // First, skip all whitespaces
        while (currentIndex < data.length && Character.isWhitespace(data[currentIndex])) {
            currentIndex++;
        }

        // Valid tag name is either a "="...
        if (data[currentIndex] == '=') {
            sb.append(data[currentIndex]);
            currentIndex++;
            token = new SmartScriptToken(SmartScriptTokenType.TAG_NAME, sb.toString());
            return token;
        }

        // ...or a letter followed by letters, digits or underscores.
        if (Character.isLetter(data[currentIndex])) {
            while (currentIndex < data.length && (
                    Character.isLetter(data[currentIndex]) ||
                    Character.isDigit(data[currentIndex]) ||
                    data[currentIndex] == '_'
            )) {
                        sb.append(data[currentIndex]);
                        currentIndex++;
            }
            token = new SmartScriptToken(SmartScriptTokenType.TAG_NAME, sb.toString());
            return token;
        }

        // Else, the tag name is invalid.
        throw new SmartScriptLexerException("Invalid tag name at index " + currentIndex + ".");
    }

    /**
     * Generates the next token in the TAG_DEF state.
     *
     * @return next token
     * @throws SmartScriptLexerException if an error occurs
     */
    private SmartScriptToken tagDefState() throws SmartScriptLexerException {
        // First skip all whitespaces
        while (currentIndex < data.length && Character.isWhitespace(data[currentIndex])) {
            currentIndex++;
        }

        // If the next character is a closing tag, generate the closing tag token.
        if (data[currentIndex] == '$' && currentIndex + 1 < data.length && data[currentIndex + 1] == '}') {
            token = new SmartScriptToken(SmartScriptTokenType.END_TAG_DEF, null);
            currentIndex += 2;
            return token;
        }

        // Else, generate the next token based on the current character,
        // the options for the token are a variable, a number (integer or double), a string, a function or an operator.

        // If the current character is a letter, the token is a variable.
        if (Character.isLetter(data[currentIndex])) {
            return variableToken();
        }

        // If the current character is a digit, the token is a number.
        if (Character.isDigit(data[currentIndex])) {
            return numberToken();
        }

        // If the current character is a double quote, the token is a string.
        if (data[currentIndex] == '"') {
            return stringToken();
        }

        // If the current character is a '@', the token is a function.
        if (data[currentIndex] == '@') {
            return functionToken();
        }

        // Now for the operators: +, -, *, /, ^.
        switch (data[currentIndex]) {
            case '+' -> {
                currentIndex++;
                token = new SmartScriptToken(SmartScriptTokenType.OPERATOR, "+");
                return token;
            }
            case '-' -> {
                // Check if the '-' is a part of a negative number.
                if (currentIndex + 1 < data.length && Character.isDigit(data[currentIndex + 1])) {
                    return numberToken();
                }
                // Else, it is an operator.
                currentIndex++;
                token = new SmartScriptToken(SmartScriptTokenType.OPERATOR, "-");
                return token;
            }
            case '*' -> {
                currentIndex++;
                token = new SmartScriptToken(SmartScriptTokenType.OPERATOR, "*");
                return token;
            }
            case '/' -> {
                currentIndex++;
                token = new SmartScriptToken(SmartScriptTokenType.OPERATOR, "/");
                return token;
            }
            case '^' -> {
                currentIndex++;
                token = new SmartScriptToken(SmartScriptTokenType.OPERATOR, "^");
                return token;
            }
            default -> {
            }
        }

        // Else, throw an exception.
        throw new SmartScriptLexerException("Invalid tag definition at index " + currentIndex + ".");
    }

    /**
     * Generates the next variable token.
     *
     * @return next variable token
     * @throws SmartScriptLexerException if an error occurs
     */
    private SmartScriptToken variableToken() throws SmartScriptLexerException {
        StringBuilder sb = new StringBuilder();
        // Variable name must start with a letter, followed by letters, digits or underscores.
        if (Character.isLetter(data[currentIndex])) {
            while (currentIndex < data.length && (
                    Character.isLetter(data[currentIndex]) ||
                    Character.isDigit(data[currentIndex]) ||
                    data[currentIndex] == '_'
            )) {
                        sb.append(data[currentIndex]);
                        currentIndex++;
            }
        } else {
            throw new SmartScriptLexerException("Invalid variable name at index " + currentIndex + ".");
        }
        token = new SmartScriptToken(SmartScriptTokenType.VARIABLE, sb.toString());
        return token;
    }

    /**
     * Generates the next number token.
     *
     * @return next number token
     * @throws SmartScriptLexerException if an error occurs
     */
    private SmartScriptToken numberToken() {
        StringBuilder sb = new StringBuilder();
        // Check for a leading minus sign.
        if (data[currentIndex] == '-') {
            sb.append(data[currentIndex]);
            currentIndex++;
        }
        // Check for digits before the dot.
        while (currentIndex < data.length && Character.isDigit(data[currentIndex])) {
            sb.append(data[currentIndex]);
            currentIndex++;
        }
        // Check if a dot follows.
        if (currentIndex < data.length && data[currentIndex] == '.') {
            sb.append(data[currentIndex]);
            currentIndex++;
            // Check if there are more digits after the dot.
            if (currentIndex < data.length && Character.isDigit(data[currentIndex])) {
                while (currentIndex < data.length && Character.isDigit(data[currentIndex])) {
                    sb.append(data[currentIndex]);
                    currentIndex++;
                }
            } else {
                // Else, add a zero after the dot, just in case.
                sb.append('0');
            }
            // Return the double token if the number can be parsed as a double.
            try {
                token = new SmartScriptToken(SmartScriptTokenType.DOUBLE, Double.parseDouble(sb.toString()));
                return token;
            } catch (NumberFormatException e) {
                throw new SmartScriptLexerException("Invalid double number (cannot be parsed) at index " + currentIndex + ".");
            }
        }
        // Else, return the integer token if the number can be parsed as an integer.
        try {
            token = new SmartScriptToken(SmartScriptTokenType.INTEGER, Integer.parseInt(sb.toString()));
            return token;
        } catch (NumberFormatException e) {
            throw new SmartScriptLexerException("Invalid integer number (cannot be parsed) at index " + currentIndex + ".");
        }
    }

    /**
     * Generates the next string token.
     *
     * @return next string token
     * @throws SmartScriptLexerException if an error occurs
     */
    private SmartScriptToken stringToken() throws SmartScriptLexerException {
        StringBuilder sb = new StringBuilder();
        // Skip the opening double quote.
        currentIndex++;
        // Check for escaped characters.
        while (currentIndex < data.length && data[currentIndex] != '"') {
            if (data[currentIndex] == '\\') {
                if (currentIndex + 1 >= data.length) {
                    throw new SmartScriptLexerException("Invalid escape sequence at index " + currentIndex + ".");
                }
                switch (data[currentIndex + 1]) {
                    case '\\' -> {
                        sb.append('\\');
                        currentIndex += 2;
                        continue;
                    }
                    case '"' -> {
                        sb.append(data[currentIndex + 1]);
                        currentIndex += 2;
                        continue;
                    }
                    case 'n' -> {
                        sb.append('\n');
                        currentIndex += 2;
                        continue;
                    }
                    case 'r' -> {
                        sb.append('\r');
                        currentIndex += 2;
                        continue;
                    }
                    case 't' -> {
                        sb.append('\t');
                        currentIndex += 2;
                        continue;
                    }
                    default ->
                            throw new SmartScriptLexerException("Invalid escape sequence at index " + currentIndex + ".");
                }
            }
            sb.append(data[currentIndex]);
            currentIndex++;
        }
        // Skip the closing double quote.
        currentIndex++;
        token = new SmartScriptToken(SmartScriptTokenType.STRING, sb.toString());
        return token;
    }

    /**
     * Generates the next function token.
     *
     * @return next function token
     * @throws SmartScriptLexerException if an error occurs
     */
    private SmartScriptToken functionToken() throws SmartScriptLexerException {
        StringBuilder sb = new StringBuilder();
        // Skip the '@' character.
        currentIndex++;
        // Function name must start with a letter, followed by letters, digits or underscores.
        if (Character.isLetter(data[currentIndex])) {
            while (currentIndex < data.length && (
                    Character.isLetter(data[currentIndex]) ||
                    Character.isDigit(data[currentIndex]) ||
                    data[currentIndex] == '_'
            )) {
                        sb.append(data[currentIndex]);
                        currentIndex++;
            }
        } else {
            throw new SmartScriptLexerException("Invalid function name at index " + currentIndex + ".");
        }
        token = new SmartScriptToken(SmartScriptTokenType.FUNCTION, sb.toString());
        return token;
    }
}
