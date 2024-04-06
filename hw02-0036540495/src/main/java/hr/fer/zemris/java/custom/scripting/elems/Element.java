package hr.fer.zemris.java.custom.scripting.elems;

import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

/**
 * Base class for all elements produced by {@link SmartScriptParser}.
 *
 * @see ElementConstantDouble
 * @see ElementConstantInteger
 * @see ElementFunction
 * @see ElementOperator
 * @see ElementString
 * @see ElementVariable
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public sealed class Element permits
        ElementConstantDouble,
        ElementConstantInteger,
        ElementFunction,
        ElementOperator, ElementString, ElementVariable {
    /**
     * Returns the element as text.
     *
     * @return string representation of the element
     */
    public String asText() {
        return "";
    }
}
