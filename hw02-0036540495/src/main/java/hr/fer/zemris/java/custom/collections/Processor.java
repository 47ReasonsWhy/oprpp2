package hr.fer.zemris.java.custom.collections;

/**
 * A functional interface which represents a general processor of objects.
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public interface Processor {
    /**
     * Processes the given value.
     *
     * @param value value to be processed
     */
    void process(Object value);
}
