package hr.fer.zemris.java.custom.scripting.elems;

import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

/**
 * An element representing an operator parsed by {@link SmartScriptParser}.
 * <p>
 * Valid operators are +, -, *, /, ^.
 *
 * @see Element
 * @see ElementConstantDouble
 * @see ElementConstantInteger
 * @see ElementFunction
 * @see ElementString
 * @see ElementVariable
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public non-sealed class ElementOperator extends Element {
    /**
     * Symbol of the operator.
     */
    private String symbol;

    /**
     * Creates an instance of ElementOperator with the given symbol.
     *
     * @param symbol symbol of the operator
     */
    public ElementOperator(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the symbol of the operator.
     *
     * @return symbol of the operator
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * @see Element#asText()
     */
    @Override
    public String asText() {
        return symbol;
    }

    /**
     * Checks if the given object is equal to the current instance of ElementOperator.
     * <p>
     * The given object is equal to this ElementOperator if it itself is an instance of ElementOperator
     * and if their symbols are equal.
     *
     * @param o object to be compared to the current instance of ElementOperator
     * @return true if the given object is equal to the current instance of ElementOperator, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ElementOperator that)) return false;

        return symbol.equals(that.symbol);
    }
}
