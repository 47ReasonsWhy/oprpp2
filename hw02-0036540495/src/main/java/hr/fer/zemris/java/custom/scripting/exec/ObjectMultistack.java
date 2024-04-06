package hr.fer.zemris.java.custom.scripting.exec;

import java.util.HashMap;
import java.util.Map;

/**
 * A "multistack" collection that stores multiple values for the same key on a stack-like basis.
 * Each key is associated with a stack of values. The class provides methods for pushing, popping,
 * peeking and checking if the stack is empty.
 *
 * @see ValueWrapper
 * @see MultistackEntry
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public class ObjectMultistack {
    /**
     * Internal map.
     */
    private final Map<String, MultistackEntry> map;

    /**
     * Default constructor.
     */
    public ObjectMultistack() {
        map = new HashMap<>();
    }

    /**
     * Pushes a value to the stack associated with the given key.
     *
     * @param keyName key
     * @param valueWrapper value to push
     */
    public void push(String keyName, ValueWrapper valueWrapper) {
        MultistackEntry toPush = new MultistackEntry();
        toPush.value = valueWrapper;
        toPush.next = map.get(keyName);
        map.put(keyName, toPush);
    }

    /**
     * Pops a value from the stack associated with the given key.
     *
     * @param keyName key
     * @return popped value
     * @throws RuntimeException if the stack is empty
     */
    public ValueWrapper pop(String keyName) {
        MultistackEntry popped = map.get(keyName);
        if (popped == null) {
            throw new RuntimeException("Multistack is empty.");
        }
        map.put(keyName, popped.next);
        return popped.value;
    }

    /**
     * Peeks at the value on the top of the stack associated with the given key.
     *
     * @param keyName key
     * @return peeked value
     * @throws RuntimeException if the stack is empty
     */
    public ValueWrapper peek(String keyName) {
        MultistackEntry peeked = map.get(keyName);
        if (peeked == null) {
            throw new RuntimeException("Multistack is empty.");
        }
        return peeked.value;
    }

    /**
     * Checks if the stack associated with the given key is empty.
     *
     * @param keyName key
     * @return true if the stack is empty, false otherwise
     */
    public boolean isEmpty(String keyName) {
        return map.get(keyName) == null;
    }

    /**
     * A single entry in the multistack.
     */
    static class MultistackEntry {
        /**
         * Value.
         */
        private ValueWrapper value;

        /**
         * Next entry.
         */
        private MultistackEntry next;
    }
}
