package hr.fer.zemris.java.custom.scripting.exec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ObjectMultistackTest {
    ObjectMultistack multistack;

    @BeforeEach
    public void setUp() {
        multistack = new ObjectMultistack();
    }

    @Test
    public void testPushAndPeek() {
        ValueWrapper value = new ValueWrapper(2000);
        multistack.push("year", value);
        assertEquals(value, multistack.peek("year"));
    }

    @Test
    public void testPushAndPop() {
        ValueWrapper value = new ValueWrapper(2000);
        multistack.push("year", value);
        assertEquals(value, multistack.pop("year"));
    }

    @Test
    public void testIsEmpty() {
        assertTrue(multistack.isEmpty("year"));
        multistack.push("year", new ValueWrapper(2000));
        assertFalse(multistack.isEmpty("year"));
    }

    @Test
    public void testPopEmptyStack() {
        assertThrows(RuntimeException.class, () -> multistack.pop("year"));
    }

    @Test
    public void testPeekEmptyStack() {
        assertThrows(RuntimeException.class, () -> multistack.peek("year"));
    }

    @Test
    public void testMultipleValuesOnSameKey() {
        ValueWrapper value1 = new ValueWrapper(2000);
        ValueWrapper value2 = new ValueWrapper(2001);
        multistack.push("year", value1);
        multistack.push("year", value2);
        assertEquals(value2, multistack.pop("year"));
        assertEquals(value1, multistack.pop("year"));
    }

    @Test
    public void testMultipleKeys() {
        ValueWrapper value1 = new ValueWrapper(2000);
        ValueWrapper value2 = new ValueWrapper(2001);
        multistack.push("year", value1);
        multistack.push("year", value2);
        ValueWrapper value3 = new ValueWrapper(2002);
        ValueWrapper value4 = new ValueWrapper(2003);
        multistack.push("year2", value3);
        multistack.push("year2", value4);
        assertEquals(value2, multistack.pop("year"));
        assertEquals(value4, multistack.pop("year2"));
        assertEquals(value3, multistack.pop("year2"));
        assertEquals(value1, multistack.pop("year"));
        assertTrue(multistack.isEmpty("year"));
        assertTrue(multistack.isEmpty("year2"));
    }
}
