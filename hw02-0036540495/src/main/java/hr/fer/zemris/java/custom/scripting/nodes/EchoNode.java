package hr.fer.zemris.java.custom.scripting.nodes;

import hr.fer.zemris.java.custom.scripting.elems.Element;

import java.util.Arrays;

/**
 * A node representing a command which generates some textual output dynamically.
 *
 * @see Node
 *
 * @version 2.0
 * @author Marko Šelendić
 */
public class EchoNode extends Node {
    /**
     * Elements of the echo node.
     */
    private final Element[] elements;

    /**
     * Constructs a new echo node with given elements.
     *
     * @param elements elements of the echo node
     *
     * @throws NullPointerException if elements are null
     */
    public EchoNode(Element[] elements) {
        if (elements == null) {
            throw new NullPointerException("Elements must not be null.");
        }
        this.elements = elements;
    }

    /**
     * Returns elements of the echo node.
     *
     * @return elements of the echo node
     */
    public Element[] getElements() {
        return elements;
    }

    /**
     * Returns a string representation of the echo node.
     * <p>
     * Format: {$= element1 element2 ... elementN $}
     *
     * @return a string representation of the echo node
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{$= ");
        for (Element element : elements) {
            sb.append(element.asText()).append(" ");
        }
        sb.append("$}");
        return sb.toString();
    }

    /**
     * Checks if the given object is equal to the echo node.
     * <p>
     * Two echo nodes are equal if they have the same elements.
     *
     * @param o object to be compared to the echo node
     * @return true if the given object is equal to the echo node, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EchoNode echoNode)) return false;

        if (elements.length != echoNode.elements.length) return false;
        for (int i = 0; i < elements.length; i++) {
            if (!elements[i].equals(echoNode.elements[i])) return false;
        }
        return true;
    }

    /**
     * Returns the hash code of the echo node.
     *
     * @return hash code of the echo node
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(elements);
    }

    /**
     * Accepts the given visitor.
     *
     * @param visitor visitor
     */
    @Override
    public void accept(INodeVisitor visitor) {
        visitor.visitEchoNode(this);
    }
}
