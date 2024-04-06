package hr.fer.zemris.java.custom.scripting.nodes;

import hr.fer.zemris.java.custom.collections.ArrayIndexedCollection;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

/**
 * Base class for nodes in {@link SmartScriptParser} tree.
 *
 * @version 2.0
 * @author Marko Šelendić
 */
public abstract class Node {
    /**
     * Internally managed collection of children.
     */
    private ArrayIndexedCollection children;

    /**
     * Adds given child to an internally managed collection of children.
     *
     * @param child child node to be added
     */
    public void addChildNode(Node child) {
        if (children == null) {
            children = new ArrayIndexedCollection();
        }
        children.add(child);
    }

    /**
     * Returns a number of direct children.
     *
     * @return number of direct children
     */
    public int numberOfChildren() {
        if (children == null) {
            return 0;
        }
        return children.size();
    }

    /**
     * Returns direct child node at given index.
     *
     * @param index index of child to be returned
     * @return selected child node
     * @throws IndexOutOfBoundsException if index is invalid
     */
    public Node getChild(int index) {
        return (Node) children.get(index);
    }

    /**
     * Accepts the given visitor.
     *
     * @param visitor visitor to be accepted
     */
    public abstract void accept(INodeVisitor visitor);
}
