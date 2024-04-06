package hr.fer.zemris.java.custom.scripting.parser;

import hr.fer.zemris.java.custom.collections.ArrayIndexedCollection;
import hr.fer.zemris.java.custom.collections.ObjectStack;
import hr.fer.zemris.java.custom.scripting.elems.*;
import hr.fer.zemris.java.custom.scripting.lexer.*;
import hr.fer.zemris.java.custom.scripting.nodes.*;

/**
 * A parser that generates a document model from the given input text.
 *
 * @see SmartScriptLexer
 * @see SmartScriptParserException
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public class SmartScriptParser {

    /**
     * Lexer that generates tokens from the input text.
     */
    private final SmartScriptLexer lexer;

    /**
     * Document model.
     */
    private final DocumentNode documentNode;

    /**
     * Constructs a new parser that generates a document model from the given input text.
     *
     * @param text input text
     * @throws SmartScriptParserException if the input text is invalid
     */
    public SmartScriptParser(String text) {
        lexer = new SmartScriptLexer(text);
        documentNode = new DocumentNode();
        try {
            parse();
        } catch (SmartScriptLexerException e) {
            throw new SmartScriptParserException(e.getMessage());
        }
    }

    /**
     * Returns the generated document model.
     *
     * @return generated document model
     */
    public DocumentNode getDocumentNode() {
        return documentNode;
    }

    /**
     * Parses the input text and generates a document model.
     *
     * @throws SmartScriptParserException if the input text is invalid
     */
    private void parse() throws SmartScriptParserException {
        ObjectStack stack = new ObjectStack();
        stack.push(documentNode);
        SmartScriptToken token;

        while (true) {
            token = lexer.nextToken();

            if (token.type() == SmartScriptTokenType.EOF) break;

            if (token.type() == SmartScriptTokenType.TEXT) {
                ((Node) stack.peek()).addChildNode(new TextNode((String) token.value()));
            } else if (token.type() == SmartScriptTokenType.START_TAG_DEF) {
                lexer.setState(SmartScriptLexerState.TAG_NAME);
            } else if (token.type() == SmartScriptTokenType.TAG_NAME) {
                lexer.setState(SmartScriptLexerState.TAG_DEF);
                if (token.value().equals("=")) {
                    ArrayIndexedCollection elements = new ArrayIndexedCollection();
                    while (true) {
                        token = lexer.nextToken();
                        if (token.type() == SmartScriptTokenType.END_TAG_DEF) {
                            lexer.setState(SmartScriptLexerState.TEXT);
                            break;
                        } else if (token.type() == SmartScriptTokenType.VARIABLE ||
                                token.type() == SmartScriptTokenType.STRING ||
                                token.type() == SmartScriptTokenType.INTEGER ||
                                token.type() == SmartScriptTokenType.DOUBLE ||
                                token.type() == SmartScriptTokenType.FUNCTION ||
                                token.type() == SmartScriptTokenType.OPERATOR) {
                            elements.add(newElement(token));
                        } else {
                            throw new SmartScriptParserException("Invalid element type in ECHO tag at position " + lexer.getCurrentIndex() + ".");
                        }
                    }
                    Element[] elementsArray = new Element[elements.size()];
                    for (int i = 0; i < elements.size(); i++) {
                        elementsArray[i] = (Element) elements.get(i);
                    }
                    ((Node) stack.peek()).addChildNode(new EchoNode(elementsArray));
                } else if (token.value().toString().equalsIgnoreCase("FOR")) {
                    ArrayIndexedCollection elements = new ArrayIndexedCollection();
                    while (true) {
                        token = lexer.nextToken();
                        if (elements.isEmpty() && token.type() != SmartScriptTokenType.VARIABLE) {
                            throw new SmartScriptParserException(
                                "Expecting a variable on the first position in FOR tag at position " +
                                lexer.getCurrentIndex() + " but got " + token.type() + "."
                            );
                        }
                        if (token.type() == SmartScriptTokenType.END_TAG_DEF) {
                            lexer.setState(SmartScriptLexerState.TEXT);
                            break;
                        } else if (token.type() == SmartScriptTokenType.VARIABLE ||
                                    token.type() == SmartScriptTokenType.STRING ||
                                    token.type() == SmartScriptTokenType.INTEGER ||
                                    token.type() == SmartScriptTokenType.DOUBLE) {
                            elements.add(newElement(token));
                        } else {
                            throw new SmartScriptParserException("Invalid element type in FOR tag at position " + lexer.getCurrentIndex() + ".");
                        }
                    }
                    if (elements.size() != 3 && elements.size() != 4) {
                        throw new SmartScriptParserException("Invalid number of elements in FOR tag at position " + lexer.getCurrentIndex() +
                                                            ". Expected 3 or 4 elements but got " + elements.size() + ".");
                    }
                    Element[] elementsArray = new Element[4];
                    for (int i = 0; i < elements.size(); i++) {
                        elementsArray[i] = (Element) elements.get(i);
                    }
                    ForLoopNode forLoopNode = new ForLoopNode((ElementVariable) elementsArray[0], elementsArray[1], elementsArray[2], elementsArray[3]);
                    ((Node) stack.peek()).addChildNode(forLoopNode);
                    stack.push(forLoopNode);
                } else if (token.value().toString().equalsIgnoreCase("END")) {
                    stack.pop();
                    if (stack.isEmpty()) {
                        throw new SmartScriptParserException("Too many END tags.");
                    }
                } else {
                    throw new SmartScriptParserException("Invalid tag name at position " + lexer.getCurrentIndex() + ": " + token.value() + ".");
                }
            } else if (token.type() == SmartScriptTokenType.END_TAG_DEF) {
                lexer.setState(SmartScriptLexerState.TEXT);
            } else {
                throw new SmartScriptParserException("Invalid token type at position " + lexer.getCurrentIndex() + ": " + token.type() + ".");
            }
        }
        if (stack.size() != 1) {
            throw new SmartScriptParserException("Too few END tags.");
        }
    }

    /**
     * Creates a new element from the given token.
     *
     * @param token token
     * @return new element
     */
    private Element newElement(SmartScriptToken token) {
        return switch (token.type()) {
            case VARIABLE -> new ElementVariable((String) token.value());
            case STRING -> new ElementString((String) token.value());
            case INTEGER -> new ElementConstantInteger((int) token.value());
            case DOUBLE -> new ElementConstantDouble((double) token.value());
            case FUNCTION -> new ElementFunction((String) token.value());
            case OPERATOR -> new ElementOperator((String) token.value());
            default -> throw new SmartScriptParserException("Invalid element type.");
        };
    }
}
