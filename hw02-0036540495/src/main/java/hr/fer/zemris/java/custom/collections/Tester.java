package hr.fer.zemris.java.custom.collections;

/**
 * A functional interface which represents a general tester of objects.
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public interface Tester {
    /**
     * Tests if the given object satisfies the condition.
     *
     * @param obj object to be tested
     * @return true if the given object satisfies the condition, false otherwise
     */
    boolean test(Object obj);
}
