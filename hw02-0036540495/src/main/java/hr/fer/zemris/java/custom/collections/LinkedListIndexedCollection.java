package hr.fer.zemris.java.custom.collections;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

/**
 * Collection of objects implemented as an indexed linked list.
 * <p>
 * Duplicate elements are allowed.
 * Storage of null references is not allowed.
 *
 * @see List
 * @see Collection
 * @see ElementsGetter
 *
 * @version 2.0
 * @author Marko Šelendić
 */
public class LinkedListIndexedCollection implements List {

    private static class ListNode {
        /**
         * Reference to the previous node in the list.
         */
        ListNode previous;

        /**
         * Reference to the next node in the list.
         */
        ListNode next;

        /**
         * Value of the node.
         */
        Object value;

        /**
         * Creates a new node with the given value.
         *
         * @param value value of the node
         */
        ListNode(Object value) {
            this.previous = null;
            this.next = null;
            this.value = value;
        }
    }

    /**
     * Current number of elements in the collection.
     */
    private int size;

    /**
     * Reference to the first node of the list.
     */
    private ListNode first;

    /**
     * Reference to the last node of the list.
     */
    private ListNode last;

    /**
     * Number of modifications made to the collection.
     */
    private long modificationCount = 0;

    /**
     * Creates an empty collection.
     */
    public LinkedListIndexedCollection() {
        this.size = 0;
        this.first = null;
        this.last = null;
    }

    /**
     * Creates a new collection and copies all elements from the given collection into it.
     *
     * @param other collection whose elements are copied into this collection
     */
    public LinkedListIndexedCollection(Collection other) {
        this();
        this.addAll(other);
    }

    /**
     * @see Collection#size()
     */
    @Override
    public int size() {
        return this.size;
    }

    /**
     * @see Collection#add(Object)
     * @throws NullPointerException if value is null
     */
    @Override
    public void add(Object value) {
        if (value == null) {
            throw new NullPointerException("Value must not be null.");
        }

        ListNode newNode = new ListNode(value);

        if (this.isEmpty()) {
            this.first = newNode;
        } else {
            this.last.next = newNode;
            newNode.previous = this.last;
        }
        this.last = newNode;
        this.size++;
        modificationCount++;
    }

    /**
     * @see Collection#contains(Object)
     */
    @Override
    public boolean contains(Object value) {
        return this.indexOf(value) != -1;
    }

    /**
     * @see Collection#remove(Object)
     */
    @Override
    public boolean remove(Object value) {
        int index = this.indexOf(value);
        if (index == -1) {
            return false;
        }

        this.remove(index);
        modificationCount++;
        return true;
    }

    /**
     * @see Collection#toArray()
     */
    @Override
    public Object[] toArray() {
        Object[] array = new Object[this.size];

        ListNode current = this.first;
        for (int i = 0; i < this.size; i++) {
            array[i] = current.value;
            current = current.next;
        }

        return array;
    }

    /**
     * @see List#get(int)
     */
    @Override
    public Object get(int index) {
        if (index < 0 || index > this.size - 1) {
            throw new IndexOutOfBoundsException("Index must be between 0 and size-1.");
        }

        ListNode node = findNodeAtIndex(index);
        return node.value;
    }

    /**
     * @see Collection#clear()
     */
    @Override
    public void clear() {
        this.first = null;
        this.last = null;
        this.size = 0;
        modificationCount++;
    }

    /**
     * @see List#insert(Object, int)
     */
    @Override
    public void insert(Object value, int position) {
        if (value == null) {
            throw new NullPointerException("Value must not be null.");
        }

        if (position < 0 || position > this.size) {
            throw new IndexOutOfBoundsException("Position must be between 0 and size.");
        }

        if (position == this.size) {
            this.add(value);
            return;
        }

        ListNode newNode = new ListNode(value);

        if (position == 0) {
            newNode.next = this.first;
            this.first.previous = newNode;
            this.first = newNode;
        } else {
            ListNode node = findNodeAtIndex(position);
            newNode.previous = node.previous;
            newNode.next = node;
            node.previous.next = newNode;
            node.previous = newNode;
        }

        this.size++;
        modificationCount++;
    }

    /**
     * Returns the node at the given index.
     *
     * @param index index of the node to be returned
     * @return node at the given index
     */
    private ListNode findNodeAtIndex(int index) {
        ListNode current;
        if (index < this.size / 2) {
            current = this.first;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            current = this.last;
            for (int i = this.size - 1; i > index; i--) {
                current = current.previous;
            }
        }
        return current;
    }

    /**
     * @see List#indexOf(Object)
     */
    @Override
    public int indexOf(Object value) {
        if (value == null) {
            return -1;
        }

        ListNode current = this.first;
        for (int i = 0; i < this.size; i++) {
            if (current.value.equals(value)) {
                return i;
            }
            current = current.next;
        }

        return -1;
    }

    /**
     * @see List#remove(int)
     */
    @Override
    public void remove(int index) {
        if (index < 0 || index > this.size - 1) {
            throw new IndexOutOfBoundsException("Index must be between 0 and size-1.");
        }

        ListNode node = findNodeAtIndex(index);

        if (index == 0) {
            this.first = node.next;
        } else if (index == this.size - 1) {
            this.last = node.previous;
        } else {
            node.previous.next = node.next;
            node.next.previous = node.previous;
        }

        this.size--;
        modificationCount++;
    }

    private static class LinkedListElementsGetter implements ElementsGetter {
        /**
         * Collection whose elements ElementsGetter will be getting.
         */
        private final LinkedListIndexedCollection collection;

        /**
         * Current node ElementsGetter has reached in the collection.
         */
        private ListNode node;

        /**
         * Number of modifications made to the collection when this ElementsGetter was created.
         */
        private final long savedModificationCount;

        /**
         * Creates a new ElementsGetter for the given collection.
         *
         * @param collection collection whose elements ElementsGetter will be getting
         */
        public LinkedListElementsGetter(LinkedListIndexedCollection collection) {
            this.collection = collection;
            this.node = collection.first;
            this.savedModificationCount = collection.modificationCount;
        }

        /**
         * @see ElementsGetter#hasNextElement()
         * @throws ConcurrentModificationException if the collection has been modified after ElementsGetter was created
         */
        @Override
        public boolean hasNextElement() {
            if (this.savedModificationCount != this.collection.modificationCount) {
                throw new ConcurrentModificationException("The collection has been modified.");
            }
            return this.node != null;
        }

        /**
         * @see ElementsGetter#getNextElement()
         * @throws ConcurrentModificationException if the collection has been modified after ElementsGetter was created
         *
         */
        @Override
        public Object getNextElement() {
            if (this.savedModificationCount != this.collection.modificationCount) {
                throw new ConcurrentModificationException("The collection has been modified.");
            }

            if (!this.hasNextElement()) {
                throw new NoSuchElementException("The collection has no more elements.");
            }

            Object value = this.node.value;
            this.node = this.node.next;
            return value;
        }
    }

    @Override
    public ElementsGetter createElementsGetter() {
        return new LinkedListElementsGetter(this);
    }
}
