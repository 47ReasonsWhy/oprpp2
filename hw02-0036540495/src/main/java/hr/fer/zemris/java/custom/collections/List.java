package hr.fer.zemris.java.custom.collections;

/**
 * An interface that represents an indexed collection (list) of objects.
 *
 * @see ArrayIndexedCollection
 * @see LinkedListIndexedCollection
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public interface List extends Collection {
    /**
     * Returns the object that is stored in the list at position index.
     *
     * @param index position of the object to be returned
     * @return object of the list at position index
     * @throws IndexOutOfBoundsException if given index is outside the range of the list
     */
    Object get(int index);

    /**
     * Inserts (does not overwrite) the given value at the given position in the list.
     * Elements starting from this position are shifted one position upwards.
     *
     * @param value    object to be inserted
     * @param position position at which the object will be inserted
     * @throws NullPointerException     if the given value is null
     * @throws IndexOutOfBoundsException if position is outside the range of the list
     */
    void insert(Object value, int position);

    /**
     * Searches the collection and returns the index of the first occurrence of the given value
     *
     * @param value object to be searched for
     * @return index of the first occurrence of the given value or -1 if the value is not found or if the value is null
     */
    int indexOf(Object value);

    /**
     * Removes the element at the specified index from the collection.
     * Shifts all subsequent elements one position downwards.
     *
     * @param index position of the object to be removed
     * @throws IndexOutOfBoundsException if index is outside the range of the list
     */
    void remove(int index);
}
