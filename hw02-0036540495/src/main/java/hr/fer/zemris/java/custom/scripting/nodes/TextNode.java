package hr.fer.zemris.java.custom.scripting.nodes;

/**
 * A node representing a piece of textual data.
 *
 * @see Node
 *
 * @version 2.0
 * @author Marko Šelendić
 */
public class TextNode extends Node {
    /**
     * Textual data stored in this node.
     */
    private final String text;

    /**
     * Constructs a new text node with given text.
     *
     * @param text text to be stored in this node
     */
    public TextNode(String text) {
        this.text = text;
    }

    /**
     * Returns the text stored in this node.
     *
     * @return text stored in this node
     */
    public String getText() {
        return text;
    }

    /**
     * @see TextNode#getText()
     */
    @Override
    public String toString() {
        return text
                .replaceAll("\\\\", "\\\\\\\\")
                .replaceAll("\\{", "\\\\{");
    }

    /**
     * Checks if the two text nodes are equal.
     * Two text nodes are equal if they contain the same text.
     *
     * @param o object to be compared to
     * @return true if the two text nodes are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextNode textNode)) return false;

        return text.equals(textNode.text);
    }

    /**
     * Returns the hash code of this text node.
     *
     * @return hash code of this text node
     */
    @Override
    public int hashCode() {
        return text != null ? text.hashCode() : 0;
    }

    /**
     * Accepts the given visitor.
     *
     * @param visitor visitor
     */
    @Override
    public void accept(INodeVisitor visitor) {
        visitor.visitTextNode(this);
    }
}
