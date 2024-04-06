package hr.fer.zemris.java.custom.scripting.elems;

import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

/**
 * A class that represents a variable parsed by {@link SmartScriptParser}.
 *
 * @see Element
 * @see ElementConstantDouble
 * @see ElementConstantInteger
 * @see ElementFunction
 * @see ElementOperator
 * @see ElementString
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public final class ElementVariable extends Element {
    /**
     * Name of the variable.
     */
    private String name;

    /**
     * Creates an instance of ElementVariable with the given name.
     *
     * @param name name of the variable
     */
    public ElementVariable(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the variable.
     *
     * @return name of the variable
     */
    public String getName() {
        return name;
    }

    /**
     * @see Element#asText()
     */
    @Override
    public String asText() {
        return name;
    }

    /**
     * Checks if the given object is equal to the current instance of ElementVariable.
     * <p>
     * The given object is equal to the current instance of ElementVariable if it is an instance of ElementVariable
     * and if their names are equal.
     *
     * @param o object to be checked
     * @return true if the given object is equal to the current instance of ElementVariable, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ElementVariable that)) return false;

        return name.equals(that.name);
    }
}
