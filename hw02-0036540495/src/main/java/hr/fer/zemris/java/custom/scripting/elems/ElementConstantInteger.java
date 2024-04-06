package hr.fer.zemris.java.custom.scripting.elems;

import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

/**
 * A class that represents a constant of type integer parsed by {@link SmartScriptParser}
 *
 * @see Element
 * @see ElementConstantDouble
 * @see ElementFunction
 * @see ElementOperator
 * @see ElementString
 * @see ElementVariable
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public final class ElementConstantInteger extends Element {
    /**
     * Value of the constant.
     */
    private int value;

    /**
     * Creates an instance of ElementConstantInteger with the given value.
     *
     * @param value value of the constant
     */
    public ElementConstantInteger(int value) {
        this.value = value;
    }

    /**
     * Returns the value of the constant.
     *
     * @return value of the constant
     */
    public int getValue() {
        return value;
    }

    /**
     * @see Element#asText()
     */
    @Override
    public String asText() {
        return Integer.toString(value);
    }

    /**
     * Checks if the given object is equal to this ElementConstantInteger.
     * <p>
     * The given object is equal to this ElementConstantInteger if it is an instance of ElementConstantInteger
     * and if their values are equal.
     *
     * @param obj object to be compared to this ElementConstantInteger
     * @return true if the given object is equal to this ElementConstantInteger, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
    	if(!(obj instanceof ElementConstantInteger other)) {
    		return false;
    	}
        return this.value == other.value;
    }
}
