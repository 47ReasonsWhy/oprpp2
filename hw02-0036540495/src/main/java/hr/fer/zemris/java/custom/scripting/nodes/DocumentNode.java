package hr.fer.zemris.java.custom.scripting.nodes;

/**
 * A node representing an entire document.
 *
 * @see Node
 *
 * @version 2.0
 * @author Marko Šelendić
 */
public class DocumentNode extends Node {
    /**
     * Constructs a new document node.
     */
    public DocumentNode() {
    }

    /**
     * Returns a string representation of the document node.
     *
     * @return string representation of the document node
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numberOfChildren(); i++) {
            sb.append(getChild(i).toString());
        }
        return sb.toString();
    }

    /**
     * Checks if two document nodes are equal by comparing their children.
     *
     * @param o object to be compared to
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentNode that)) return false;

        if (numberOfChildren() != that.numberOfChildren()) return false;
        for (int i = 0; i < numberOfChildren(); i++) {
            if (!getChild(i).equals(that.getChild(i))) return false;
        }
        return true;
    }

    /**
     * Returns the hash code of the document node.
     *
     * @return hash code of the document node
     */
    @Override
    public int hashCode() {
        return 0;
    }

    /**
     * Accepts the given visitor.
     *
     * @param visitor visitor
     */
    @Override
    public void accept(INodeVisitor visitor) {
        visitor.visitDocumentNode(this);
    }
}
