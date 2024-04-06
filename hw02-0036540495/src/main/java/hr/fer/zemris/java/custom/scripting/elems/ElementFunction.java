package hr.fer.zemris.java.custom.scripting.elems;

import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

/**
 * A class that represents a function parsed by {@link SmartScriptParser}.
 *
 * @see Element
 * @see ElementConstantDouble
 * @see ElementConstantInteger
 * @see ElementOperator
 * @see ElementString
 * @see ElementVariable
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public non-sealed class ElementFunction extends Element {
    /**
     * Name of the function.
     */
    private String name;

    /**
     * Creates an instance of ElementFunction with the given name.
     *
     * @param name name of the function
     */
    public ElementFunction(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the function.
     *
     * @return name of the function
     */
    public String getName() {
        return name;
    }

    /**
     * @see Element#asText()
     */
    @Override
    public String asText() {
        return "@" + name;
    }

    /**
     * Checks if the given object is equal to this ElementFunction.
     * <p>
     * They are equal only if the given object is an instance of ElementFunction
     * and if their names are equal.
     *
     * @param o object to be checked
     * @return true if the given object is equal to this ElementFunction, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ElementFunction that)) return false;

        return name.equals(that.name);
    }
}
