package hr.fer.zemris.java.custom.scripting.elems;

import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

import java.util.Objects;

/**
 * A class that represents a string parsed by {@link SmartScriptParser}.
 *
 * @see Element
 * @see ElementConstantDouble
 * @see ElementConstantInteger
 * @see ElementFunction
 * @see ElementOperator
 * @see ElementVariable
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public final class ElementString extends Element {
    /**
     * Value of the string.
     */
    private String value;

    /**
     * Creates an instance of ElementString with the given value.
     *
     * @param value value of the string
     */
    public ElementString(String value) {
        this.value = value;
    }

    /**
     * Returns the value of the string.
     *
     * @return value of the string
     */
    public String getValue() {
        return value;
    }

    /**
     * @see Element#asText()
     */
    @Override
    public String asText() {
        return "\"" + value
                .replaceAll("\\\\", "\\\\\\\\")
                .replaceAll("\"", "\\\\\"")
                .replaceAll("\n", "\\\\n")
                .replaceAll("\r", "\\\\r")
                .replaceAll("\t", "\\\\t")
                + "\"";
    }

    /**
     * Checks if the given object is equal to the current instance of ElementString.
     * <p>
     * The given object is equal to the current instance of ElementString if it is an instance of ElementString
     * and if their values are equal.
     *
     * @param o object to be checked
     * @return true if the given object is equal to the current instance of ElementString, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ElementString that)) return false;

        return Objects.equals(value, that.value);
    }
}
