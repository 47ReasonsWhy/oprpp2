package hr.fer.zemris.java.custom.scripting.exec;

/**
 * Wraps a value and provides methods for performing arithmetic operations and comparisons on it.
 * The value can be an integer, double, a string that can be parsed to a number,
 * or null (which is treated as an integer of value 0).
 * The value is stored as an Object, and the methods will automatically convert it to the appropriate type.
 * If the value is a string, it will be parsed to an integer if possible, and to a double if not.
 *
 * @see OperationPair
 *
 * @version 1.0
 * @since 1.0
 */
public class ValueWrapper {
    /**
     * The wrapped value.
     */
    private Object value;

    /**
     * Constructs a new ValueWrapper with the given value.
     *
     * @param value the value to wrap
     */
    public ValueWrapper(Object value) {
        this.value = value;
    }

    /**
     * Returns the wrapped value.
     *
     * @return the wrapped value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets the wrapped value.
     *
     * @param value the new value to wrap
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Adds the given value to the wrapped value.
     *
     * @param other the value to add
     */
    public void add(Object other) {
        OperationPair pair = new OperationPair(value, other);
        if (pair.first instanceof Integer && pair.second instanceof Integer) {
            value = (int) pair.first + (int) pair.second;
        } else {
            value = (double) pair.first + (double) pair.second;
        }
    }

    /**
     * Subtracts the given value from the wrapped value.
     *
     * @param other the value to subtract
     */
    public void subtract(Object other) {
        OperationPair pair = new OperationPair(value, other);
        if (pair.first instanceof Integer && pair.second instanceof Integer) {
            value = (int) pair.first - (int) pair.second;
        } else {
            value = (double) pair.first - (double) pair.second;
        }
    }

    /**
     * Multiplies the wrapped value by the given value.
     *
     * @param other the value to multiply by
     */
    public void multiply(Object other) {
        OperationPair pair = new OperationPair(value, other);
        if (pair.first instanceof Integer && pair.second instanceof Integer) {
            value = (int) pair.first * (int) pair.second;
        } else {
            value = (double) pair.first * (double) pair.second;
        }
    }

    /**
     * Divides the wrapped value by the given value.
     * In order to avoid exception throwing, division by zero (even if integer)
     * will result in {@link Double#POSITIVE_INFINITY}, {@link Double#NEGATIVE_INFINITY} or {@link Double#NaN}.
     *
     * @param other the value to divide by
     */
    public void divide(Object other) {
        OperationPair pair = new OperationPair(value, other);
        if (pair.first instanceof Integer && pair.second instanceof Integer && (int) pair.second != 0) {
            value = (int) pair.first / (int) pair.second;
        } else {
            value = Double.parseDouble(pair.first.toString()) / Double.parseDouble(pair.second.toString());
        }
    }

    /**
     * Compares the wrapped value with the given value.
     *
     * @param other the value to compare with
     * @return a negative integer, zero, or a positive integer as the wrapped value is less than, equal to, or greater than the given value
     */
    public int numCompare(Object other) {
        OperationPair pair = new OperationPair(value, other);
        if (pair.first instanceof Integer && pair.second instanceof Integer) {
            return Integer.compare((int) pair.first, (int) pair.second);
        } else {
            return Double.compare((double) pair.first, (double) pair.second);
        }
    }

    /**
     * A private record that holds the two values to be operated on.
     * It applies the following rules to the values, and in that order:
     * <ol>
     * <li>If a value is null, it is treated as an integer of value 0.</li>
     * <li>If a value is a string, it is parsed to an integer if possible, and to a double if not.</li>
     * <li>If a value is a double, the other value is converted to a double if it is an integer.</li>
     * </ol>
     */
    private record OperationPair(Object first, Object second) {
        /**
         * Constructs a new OperationPair with the given values,
         * while applying the rules mentioned in {@link OperationPair}
         *
         * @param first the first value
         * @param second the second value
         */
        private OperationPair {
            // Rule 1
            if (first == null) first = 0;
            if (second == null) second = 0;

            // Rule 2
            if (first instanceof String) {
                first = parseStringToNumber((String) first);
            }
            if (second instanceof String) {
                second = parseStringToNumber((String) second);
            }

            // Rule 3
            if (first instanceof Double || second instanceof Double) {
                if (first instanceof Integer) {
                    first = ((Integer) first).doubleValue();
                }
                if (second instanceof Integer) {
                    second = ((Integer) second).doubleValue();
                }
            }
        }
    }

    /**
     * Parses the given string to a number.
     * If the string can be parsed to an integer, an integer is returned.
     * If the string cannot be parsed to an integer, but can be parsed to a double, a double is returned.
     * Otherwise, a {@link RuntimeException} is thrown.
     *
     * @param string the string to parse
     * @return the parsed number
     * @throws RuntimeException if the string can neither be parsed to an integer nor to a double
     */
    private static Object parseStringToNumber(String string) throws RuntimeException {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            try {
                return Double.parseDouble(string);
            } catch (NumberFormatException e2) {
                throw new RuntimeException("Cannot parse value " + string + " to number.");
            }
        }
    }
}
