package hr.fer.zemris.java.custom.scripting.nodes;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;

import java.util.Objects;

/**
 * A node representing a for-loop construct.
 *
 * @see Node
 *
 * @version 2.0
 * @author Marko Šelendić
 */
public class ForLoopNode extends Node {
    /**
     * Variable of the for-loop.
     */
    private final ElementVariable variable;

    /**
     * Start expression of the for-loop.
     */
    private final Element startExpression;

    /**
     * End expression of the for-loop.
     */
    private final Element endExpression;

    /**
     * Step expression of the for-loop.
     */
    private final Element stepExpression;

    /**
     * Constructs a new for-loop node with given parameters.
     *
     * @param variable variable of the for-loop
     * @param startExpression start expression of the for-loop
     * @param endExpression end expression of the for-loop
     * @param stepExpression step expression of the for-loop
     *
     * @throws NullPointerException if variable, startExpression or endExpression are null
     */
    public ForLoopNode(ElementVariable variable, Element startExpression, Element endExpression, Element stepExpression) {
        if (variable == null || startExpression == null || endExpression == null) {
            throw new NullPointerException("Variable, startExpression and endExpression must not be null.");
        }
        this.variable = variable;
        this.startExpression = startExpression;
        this.endExpression = endExpression;
        this.stepExpression = stepExpression;
    }

    /**
     * Returns variable of the for-loop.
     *
     * @return variable of the for-loop
     */
    public ElementVariable getVariable() {
        return variable;
    }

    /**
     * Returns start expression of the for-loop.
     *
     * @return start expression of the for-loop
     */
    public Element getStartExpression() {
        return startExpression;
    }

    /**
     * Returns end expression of the for-loop.
     *
     * @return end expression of the for-loop
     */
    public Element getEndExpression() {
        return endExpression;
    }

    /**
     * Returns step expression of the for-loop.
     *
     * @return step expression of the for-loop
     */
    public Element getStepExpression() {
        return stepExpression;
    }

    /**
     * Returns a string representation of the for-loop node.
     * <p>
     * Format: "{$ FOR variable startExpression endExpression stepExpression $} ... {$ END $}"
     *
     * @return a string representation of the for-loop node
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{$ FOR ");
        sb.append(variable.asText());
        sb.append(" ");
        sb.append(startExpression.asText());
        sb.append(" ");
        sb.append(endExpression.asText());
        if (stepExpression != null) {
            sb.append(" ");
            sb.append(stepExpression.asText());
        }
        sb.append(" $}");
        for (int i = 0; i < numberOfChildren(); i++) {
            sb.append(getChild(i).toString());
        }
        sb.append("{$ END $}");
        return sb.toString();
    }

    /**
     * Checks if two for-loop nodes are equal.
     * <p>
     * Two for-loop nodes are equal if they have the same variable, start expression, end expression and step expression.
     *
     * @param o object to be compared to
     * @return true if two for-loop nodes are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ForLoopNode that)) return false;

        if (!variable.equals(that.variable)) return false;
        if (!startExpression.equals(that.startExpression)) return false;
        if (!endExpression.equals(that.endExpression)) return false;
        return Objects.equals(stepExpression, that.stepExpression);
    }

    /**
     * Returns the hash code value for the for-loop node.
     *
     * @return the hash code value for the for-loop node
     */
    @Override
    public int hashCode() {
        int result = variable.hashCode();
        result = 31 * result + startExpression.hashCode();
        result = 31 * result + endExpression.hashCode();
        result = 31 * result + (stepExpression != null ? stepExpression.hashCode() : 0);
        return result;
    }

    /**
     * Accepts the given visitor.
     *
     * @param visitor visitor to be accepted
     */
    @Override
    public void accept(INodeVisitor visitor) {
        visitor.visitForLoopNode(this);
    }
}

