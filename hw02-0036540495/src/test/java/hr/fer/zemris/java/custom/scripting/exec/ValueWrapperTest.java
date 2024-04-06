package hr.fer.zemris.java.custom.scripting.exec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValueWrapperTest {
    private ValueWrapper intWrapper1, intWrapper2;
    private ValueWrapper doubleWrapper1, doubleWrapper2;
    private ValueWrapper stringWrapper1, stringWrapper2;
    private ValueWrapper nullWrapper1, nullWrapper2;

    @BeforeEach
    void setUp() {
        intWrapper1 = new ValueWrapper(4); intWrapper2 = new ValueWrapper(7);
        doubleWrapper1 = new ValueWrapper(4.0); doubleWrapper2 = new ValueWrapper(7.0);
        stringWrapper1 = new ValueWrapper("4"); stringWrapper2 = new ValueWrapper("7.0e0");
        nullWrapper1 = new ValueWrapper(null); nullWrapper2 = new ValueWrapper(null);
    }

    @Test
    void testAddInt_() {

        intWrapper1.add(intWrapper2.getValue());
        assertInstanceOf(Integer.class, intWrapper1.getValue());
        assertInstanceOf(Integer.class, intWrapper2.getValue());
        assertEquals(11, intWrapper1.getValue());
        assertEquals(7, intWrapper2.getValue());

        intWrapper1.setValue(4);
        intWrapper1.add(doubleWrapper2.getValue());
        assertInstanceOf(Double.class, intWrapper1.getValue());
        assertInstanceOf(Double.class, doubleWrapper2.getValue());
        assertEquals(11.0, intWrapper1.getValue());
        assertEquals(7.0, doubleWrapper2.getValue());

        intWrapper1.setValue(4);
        intWrapper1.add(stringWrapper2.getValue());
        assertInstanceOf(Double.class, intWrapper1.getValue());
        assertInstanceOf(String.class, stringWrapper2.getValue());
        assertEquals(11.0, intWrapper1.getValue());
        assertEquals("7.0e0", stringWrapper2.getValue());

        intWrapper1.setValue(4);
        intWrapper1.add(nullWrapper2.getValue());
        assertInstanceOf(Integer.class, intWrapper1.getValue());
        assertNull(nullWrapper2.getValue());
        assertEquals(4, intWrapper1.getValue());

    }

    @Test
    void testAddDouble_() {

        doubleWrapper1.add(intWrapper2.getValue());
        assertInstanceOf(Double.class, doubleWrapper1.getValue());
        assertInstanceOf(Integer.class, intWrapper2.getValue());
        assertEquals(11.0, doubleWrapper1.getValue());
        assertEquals(7, intWrapper2.getValue());

        doubleWrapper1.setValue(4.0);
        doubleWrapper1.add(doubleWrapper2.getValue());
        assertInstanceOf(Double.class, doubleWrapper1.getValue());
        assertInstanceOf(Double.class, doubleWrapper2.getValue());
        assertEquals(11.0, doubleWrapper1.getValue());
        assertEquals(7.0, doubleWrapper2.getValue());

        doubleWrapper1.setValue(4.0);
        doubleWrapper1.add(stringWrapper2.getValue());
        assertInstanceOf(Double.class, doubleWrapper1.getValue());
        assertInstanceOf(String.class, stringWrapper2.getValue());
        assertEquals(11.0, doubleWrapper1.getValue());
        assertEquals("7.0e0", stringWrapper2.getValue());

        doubleWrapper1.setValue(4.0);
        doubleWrapper1.add(nullWrapper2.getValue());
        assertInstanceOf(Double.class, doubleWrapper1.getValue());
        assertNull(nullWrapper2.getValue());
        assertEquals(4.0, doubleWrapper1.getValue());

    }

    @Test
    void testAddString_() {

        stringWrapper1.add(intWrapper2.getValue());
        assertInstanceOf(Integer.class, stringWrapper1.getValue());
        assertInstanceOf(Integer.class, intWrapper2.getValue());
        assertEquals(11, stringWrapper1.getValue());
        assertEquals(7, intWrapper2.getValue());

        stringWrapper1.setValue("4");
        stringWrapper1.add(doubleWrapper2.getValue());
        assertInstanceOf(Double.class, stringWrapper1.getValue());
        assertInstanceOf(Double.class, doubleWrapper2.getValue());
        assertEquals(11.0, stringWrapper1.getValue());
        assertEquals(7.0, doubleWrapper2.getValue());

        stringWrapper1.setValue("4");
        stringWrapper1.add(stringWrapper2.getValue());
        assertInstanceOf(Double.class, stringWrapper1.getValue());
        assertInstanceOf(String.class, stringWrapper2.getValue());
        assertEquals(11.0, stringWrapper1.getValue());
        assertEquals("7.0e0", stringWrapper2.getValue());

        stringWrapper1.setValue("4");
        stringWrapper1.add(nullWrapper2.getValue());
        assertInstanceOf(Integer.class, stringWrapper1.getValue());
        assertNull(nullWrapper2.getValue());
        assertEquals(4, stringWrapper1.getValue());

    }

    @Test
    void testAddNull_() {

        nullWrapper1.add(intWrapper2.getValue());
        assertInstanceOf(Integer.class, nullWrapper1.getValue());
        assertNull(nullWrapper2.getValue());
        assertEquals(7, nullWrapper1.getValue());
        assertEquals(7, intWrapper2.getValue());

        nullWrapper1.setValue(null);
        nullWrapper1.add(doubleWrapper2.getValue());
        assertInstanceOf(Double.class, nullWrapper1.getValue());
        assertInstanceOf(Double.class, doubleWrapper2.getValue());
        assertEquals(7.0, nullWrapper1.getValue());
        assertEquals(7.0, doubleWrapper2.getValue());

        nullWrapper1.setValue(null);
        nullWrapper1.add(stringWrapper2.getValue());
        assertInstanceOf(Double.class, nullWrapper1.getValue());
        assertInstanceOf(String.class, stringWrapper2.getValue());
        assertEquals(7.0, nullWrapper1.getValue());
        assertEquals("7.0e0", stringWrapper2.getValue());

        nullWrapper1.setValue(null);
        nullWrapper1.add(nullWrapper2.getValue());
        assertInstanceOf(Integer.class, nullWrapper1.getValue());
        assertNull(nullWrapper2.getValue());
        assertEquals(0, nullWrapper1.getValue());

    }

    @Test
    void testAddThrows() {

        ValueWrapper v1 = new ValueWrapper("gibberish");
        ValueWrapper v2 = new ValueWrapper("4");
        assertThrows(RuntimeException.class, () -> v1.add(v2.getValue()));

        ValueWrapper v3 = new ValueWrapper("-7.0e0");
        ValueWrapper v4 = new ValueWrapper("more gibberish");
        assertThrows(RuntimeException.class, () -> v3.add(v4.getValue()));

        ValueWrapper v5 = new ValueWrapper("even more gibberish");
        ValueWrapper v6 = new ValueWrapper("the most gibberish");
        assertThrows(RuntimeException.class, () -> v5.add(v6.getValue()));

    }

    @Test
    void testSubtractInt_() {

        intWrapper1.subtract(intWrapper2.getValue());
        assertInstanceOf(Integer.class, intWrapper1.getValue());
        assertInstanceOf(Integer.class, intWrapper2.getValue());
        assertEquals(-3, intWrapper1.getValue());
        assertEquals(7, intWrapper2.getValue());

        intWrapper1.setValue(4);
        intWrapper1.subtract(doubleWrapper2.getValue());
        assertInstanceOf(Double.class, intWrapper1.getValue());
        assertInstanceOf(Double.class, doubleWrapper2.getValue());
        assertEquals(-3.0, intWrapper1.getValue());
        assertEquals(7.0, doubleWrapper2.getValue());

        intWrapper1.setValue(4);
        intWrapper1.subtract(stringWrapper2.getValue());
        assertInstanceOf(Double.class, intWrapper1.getValue());
        assertInstanceOf(String.class, stringWrapper2.getValue());
        assertEquals(-3.0, intWrapper1.getValue());
        assertEquals("7.0e0", stringWrapper2.getValue());

        intWrapper1.setValue(4);
        intWrapper1.subtract(nullWrapper2.getValue());
        assertInstanceOf(Integer.class, intWrapper1.getValue());
        assertNull(nullWrapper2.getValue());
        assertEquals(4, intWrapper1.getValue());

    }

    @Test
    void testSubtractDouble_() {

        doubleWrapper1.subtract(intWrapper2.getValue());
        assertInstanceOf(Double.class, doubleWrapper1.getValue());
        assertInstanceOf(Integer.class, intWrapper2.getValue());
        assertEquals(-3.0, doubleWrapper1.getValue());
        assertEquals(7, intWrapper2.getValue());

        doubleWrapper1.setValue(4.0);
        doubleWrapper1.subtract(doubleWrapper2.getValue());
        assertInstanceOf(Double.class, doubleWrapper1.getValue());
        assertInstanceOf(Double.class, doubleWrapper2.getValue());
        assertEquals(-3.0, doubleWrapper1.getValue());
        assertEquals(7.0, doubleWrapper2.getValue());

        doubleWrapper1.setValue(4.0);
        doubleWrapper1.subtract(stringWrapper2.getValue());
        assertInstanceOf(Double.class, doubleWrapper1.getValue());
        assertInstanceOf(String.class, stringWrapper2.getValue());
        assertEquals(-3.0, doubleWrapper1.getValue());
        assertEquals("7.0e0", stringWrapper2.getValue());

        doubleWrapper1.setValue(4.0);
        doubleWrapper1.subtract(nullWrapper2.getValue());
        assertInstanceOf(Double.class, doubleWrapper1.getValue());
        assertNull(nullWrapper2.getValue());
        assertEquals(4.0, doubleWrapper1.getValue());

    }

    @Test
    void testSubtractString_() {

        stringWrapper1.subtract(intWrapper2.getValue());
        assertInstanceOf(Integer.class, stringWrapper1.getValue());
        assertInstanceOf(Integer.class, intWrapper2.getValue());
        assertEquals(-3, stringWrapper1.getValue());
        assertEquals(7, intWrapper2.getValue());

        stringWrapper1.setValue("4");
        stringWrapper1.subtract(doubleWrapper2.getValue());
        assertInstanceOf(Double.class, stringWrapper1.getValue());
        assertInstanceOf(Double.class, doubleWrapper2.getValue());
        assertEquals(-3.0, stringWrapper1.getValue());
        assertEquals(7.0, doubleWrapper2.getValue());

        stringWrapper1.setValue("4");
        stringWrapper1.subtract(stringWrapper2.getValue());
        assertInstanceOf(Double.class, stringWrapper1.getValue());
        assertInstanceOf(String.class, stringWrapper2.getValue());
        assertEquals(-3.0, stringWrapper1.getValue());
        assertEquals("7.0e0", stringWrapper2.getValue());

        stringWrapper1.setValue("4");
        stringWrapper1.subtract(nullWrapper2.getValue());
        assertInstanceOf(Integer.class, stringWrapper1.getValue());
        assertNull(nullWrapper2.getValue());
        assertEquals(4, stringWrapper1.getValue());

    }

    @Test
    void testSubtractNull_() {

        nullWrapper1.subtract(intWrapper2.getValue());
        assertInstanceOf(Integer.class, nullWrapper1.getValue());
        assertNull(nullWrapper2.getValue());
        assertEquals(-7, nullWrapper1.getValue());
        assertEquals(7, intWrapper2.getValue());

        nullWrapper1.setValue(null);
        nullWrapper1.subtract(doubleWrapper2.getValue());
        assertInstanceOf(Double.class, nullWrapper1.getValue());
        assertInstanceOf(Double.class, doubleWrapper2.getValue());
        assertEquals(-7.0, nullWrapper1.getValue());
        assertEquals(7.0, doubleWrapper2.getValue());

        nullWrapper1.setValue(null);
        nullWrapper1.subtract(stringWrapper2.getValue());
        assertInstanceOf(Double.class, nullWrapper1.getValue());
        assertInstanceOf(String.class, stringWrapper2.getValue());
        assertEquals(-7.0, nullWrapper1.getValue());
        assertEquals("7.0e0", stringWrapper2.getValue());

        nullWrapper1.setValue(null);
        nullWrapper1.subtract(nullWrapper2.getValue());
        assertInstanceOf(Integer.class, nullWrapper1.getValue());
        assertNull(nullWrapper2.getValue());
        assertEquals(0, nullWrapper1.getValue());

    }

    @Test
    void testSubtractThrows() {

        ValueWrapper v1 = new ValueWrapper("gibberish");
        ValueWrapper v2 = new ValueWrapper("4");
        assertThrows(RuntimeException.class, () -> v1.subtract(v2.getValue()));

        ValueWrapper v3 = new ValueWrapper("-7.0e0");
        ValueWrapper v4 = new ValueWrapper("more gibberish");
        assertThrows(RuntimeException.class, () -> v3.subtract(v4.getValue()));

        ValueWrapper v5 = new ValueWrapper("even more gibberish");
        ValueWrapper v6 = new ValueWrapper("the most gibberish");
        assertThrows(RuntimeException.class, () -> v5.subtract(v6.getValue()));
    }

    @Test
    void testMultiplyInt_() {

        intWrapper1.multiply(intWrapper2.getValue());
        assertInstanceOf(Integer.class, intWrapper1.getValue());
        assertInstanceOf(Integer.class, intWrapper2.getValue());
        assertEquals(28, intWrapper1.getValue());
        assertEquals(7, intWrapper2.getValue());

        intWrapper1.setValue(4);
        intWrapper1.multiply(doubleWrapper2.getValue());
        assertInstanceOf(Double.class, intWrapper1.getValue());
        assertInstanceOf(Double.class, doubleWrapper2.getValue());
        assertEquals(28.0, intWrapper1.getValue());
        assertEquals(7.0, doubleWrapper2.getValue());

        intWrapper1.setValue(4);
        intWrapper1.multiply(stringWrapper2.getValue());
        assertInstanceOf(Double.class, intWrapper1.getValue());
        assertInstanceOf(String.class, stringWrapper2.getValue());
        assertEquals(28.0, intWrapper1.getValue());
        assertEquals("7.0e0", stringWrapper2.getValue());

        intWrapper1.setValue(4);
        intWrapper1.multiply(nullWrapper2.getValue());
        assertInstanceOf(Integer.class, intWrapper1.getValue());
        assertNull(nullWrapper2.getValue());
        assertEquals(0, intWrapper1.getValue());

    }

    @Test
    void testMultiplyDouble_() {

        doubleWrapper1.multiply(intWrapper2.getValue());
        assertInstanceOf(Double.class, doubleWrapper1.getValue());
        assertInstanceOf(Integer.class, intWrapper2.getValue());
        assertEquals(28.0, doubleWrapper1.getValue());
        assertEquals(7, intWrapper2.getValue());

        doubleWrapper1.setValue(4.0);
        doubleWrapper1.multiply(doubleWrapper2.getValue());
        assertInstanceOf(Double.class, doubleWrapper1.getValue());
        assertInstanceOf(Double.class, doubleWrapper2.getValue());
        assertEquals(28.0, doubleWrapper1.getValue());
        assertEquals(7.0, doubleWrapper2.getValue());

        doubleWrapper1.setValue(4.0);
        doubleWrapper1.multiply(stringWrapper2.getValue());
        assertInstanceOf(Double.class, doubleWrapper1.getValue());
        assertInstanceOf(String.class, stringWrapper2.getValue());
        assertEquals(28.0, doubleWrapper1.getValue());
        assertEquals("7.0e0", stringWrapper2.getValue());

        doubleWrapper1.setValue(4.0);
        doubleWrapper1.multiply(nullWrapper2.getValue());
        assertInstanceOf(Double.class, doubleWrapper1.getValue());
        assertNull(nullWrapper2.getValue());
        assertEquals(0.0, doubleWrapper1.getValue());

    }

    @Test
    void testMultiplyString_() {

        stringWrapper1.multiply(intWrapper2.getValue());
        assertInstanceOf(Integer.class, stringWrapper1.getValue());
        assertInstanceOf(Integer.class, intWrapper2.getValue());
        assertEquals(28, stringWrapper1.getValue());
        assertEquals(7, intWrapper2.getValue());

        stringWrapper1.setValue("4");
        stringWrapper1.multiply(doubleWrapper2.getValue());
        assertInstanceOf(Double.class, stringWrapper1.getValue());
        assertInstanceOf(Double.class, doubleWrapper2.getValue());
        assertEquals(28.0, stringWrapper1.getValue());
        assertEquals(7.0, doubleWrapper2.getValue());

        stringWrapper1.setValue("4");
        stringWrapper1.multiply(stringWrapper2.getValue());
        assertInstanceOf(Double.class, stringWrapper1.getValue());
        assertInstanceOf(String.class, stringWrapper2.getValue());
        assertEquals(28.0, stringWrapper1.getValue());
        assertEquals("7.0e0", stringWrapper2.getValue());

        stringWrapper1.setValue("4");
        stringWrapper1.multiply(nullWrapper2.getValue());
        assertInstanceOf(Integer.class, stringWrapper1.getValue());
        assertNull(nullWrapper2.getValue());
        assertEquals(0, stringWrapper1.getValue());

    }

    @Test
    void testMultiplyNull_() {

        nullWrapper1.multiply(intWrapper2.getValue());
        assertInstanceOf(Integer.class, nullWrapper1.getValue());
        assertNull(nullWrapper2.getValue());
        assertEquals(0, nullWrapper1.getValue());
        assertEquals(7, intWrapper2.getValue());

        nullWrapper1.setValue(null);
        nullWrapper1.multiply(doubleWrapper2.getValue());
        assertInstanceOf(Double.class, nullWrapper1.getValue());
        assertInstanceOf(Double.class, doubleWrapper2.getValue());
        assertEquals(0.0, nullWrapper1.getValue());
        assertEquals(7.0, doubleWrapper2.getValue());

        nullWrapper1.setValue(null);
        nullWrapper1.multiply(stringWrapper2.getValue());
        assertInstanceOf(Double.class, nullWrapper1.getValue());
        assertInstanceOf(String.class, stringWrapper2.getValue());
        assertEquals(0.0, nullWrapper1.getValue());

        nullWrapper1.setValue(null);
        nullWrapper1.multiply(nullWrapper2.getValue());
        assertInstanceOf(Integer.class, nullWrapper1.getValue());
        assertNull(nullWrapper2.getValue());
        assertEquals(0, nullWrapper1.getValue());

    }

    @Test
    void testMultiplyThrows() {

        ValueWrapper v1 = new ValueWrapper("gibberish");
        ValueWrapper v2 = new ValueWrapper("4");
        assertThrows(RuntimeException.class, () -> v1.multiply(v2.getValue()));

        ValueWrapper v3 = new ValueWrapper("-7.0e0");
        ValueWrapper v4 = new ValueWrapper("more gibberish");
        assertThrows(RuntimeException.class, () -> v3.multiply(v4.getValue()));

        ValueWrapper v5 = new ValueWrapper("even more gibberish");
        ValueWrapper v6 = new ValueWrapper("the most gibberish");
        assertThrows(RuntimeException.class, () -> v5.multiply(v6.getValue()));
    }

    @Test
    void testDivideInt_() {

        intWrapper1.divide(intWrapper2.getValue());
        assertInstanceOf(Integer.class, intWrapper1.getValue());
        assertInstanceOf(Integer.class, intWrapper2.getValue());
        assertEquals(0, intWrapper1.getValue());
        assertEquals(7, intWrapper2.getValue());

        intWrapper1.setValue(4);
        intWrapper1.divide(doubleWrapper2.getValue());
        assertInstanceOf(Double.class, intWrapper1.getValue());
        assertInstanceOf(Double.class, doubleWrapper2.getValue());
        assertEquals(4.0 / 7.0, intWrapper1.getValue());
        assertEquals(7.0, doubleWrapper2.getValue());

        intWrapper1.setValue(4);
        intWrapper1.divide(stringWrapper2.getValue());
        assertInstanceOf(Double.class, intWrapper1.getValue());
        assertInstanceOf(String.class, stringWrapper2.getValue());
        assertEquals(4.0 / 7.0, intWrapper1.getValue());
        assertEquals("7.0e0", stringWrapper2.getValue());

        intWrapper1.setValue(4);
        intWrapper1.divide(nullWrapper2.getValue());
        assertInstanceOf(Double.class, intWrapper1.getValue());
        assertNull(nullWrapper2.getValue());
        assertEquals(Double.POSITIVE_INFINITY, (Double) intWrapper1.getValue());

    }

    @Test
    void testDivideDouble_() {

        doubleWrapper1.divide(intWrapper2.getValue());
        assertInstanceOf(Double.class, doubleWrapper1.getValue());
        assertInstanceOf(Integer.class, intWrapper2.getValue());
        assertEquals(4.0 / 7.0, doubleWrapper1.getValue());
        assertEquals(7, intWrapper2.getValue());

        doubleWrapper1.setValue(4.0);
        doubleWrapper1.divide(doubleWrapper2.getValue());
        assertInstanceOf(Double.class, doubleWrapper1.getValue());
        assertInstanceOf(Double.class, doubleWrapper2.getValue());
        assertEquals(4.0 / 7.0, doubleWrapper1.getValue());
        assertEquals(7.0, doubleWrapper2.getValue());

        doubleWrapper1.setValue(4.0);
        doubleWrapper1.divide(stringWrapper2.getValue());
        assertInstanceOf(Double.class, doubleWrapper1.getValue());
        assertInstanceOf(String.class, stringWrapper2.getValue());
        assertEquals(4.0 / 7.0, doubleWrapper1.getValue());
        assertEquals("7.0e0", stringWrapper2.getValue());

        doubleWrapper1.setValue(4.0);
        doubleWrapper1.divide(nullWrapper2.getValue());
        assertInstanceOf(Double.class, doubleWrapper1.getValue());
        assertNull(nullWrapper2.getValue());
        assertEquals(Double.POSITIVE_INFINITY, (Double) doubleWrapper1.getValue());

    }

    @Test
    void testDivideString_() {

        stringWrapper1.divide(intWrapper2.getValue());
        assertInstanceOf(Integer.class, stringWrapper1.getValue());
        assertInstanceOf(Integer.class, intWrapper2.getValue());
        assertEquals(0, stringWrapper1.getValue());
        assertEquals(7, intWrapper2.getValue());

        stringWrapper1.setValue("4");
        stringWrapper1.divide(doubleWrapper2.getValue());
        assertInstanceOf(Double.class, stringWrapper1.getValue());
        assertInstanceOf(Double.class, doubleWrapper2.getValue());
        assertEquals(4.0 / 7.0, stringWrapper1.getValue());
        assertEquals(7.0, doubleWrapper2.getValue());

        stringWrapper1.setValue("4");
        stringWrapper1.divide(stringWrapper2.getValue());
        assertInstanceOf(Double.class, stringWrapper1.getValue());
        assertInstanceOf(String.class, stringWrapper2.getValue());
        assertEquals(4.0 / 7.0, stringWrapper1.getValue());
        assertEquals("7.0e0", stringWrapper2.getValue());

        stringWrapper1.setValue("4");
        stringWrapper1.divide(nullWrapper2.getValue());
        assertInstanceOf(Double.class, stringWrapper1.getValue());
        assertNull(nullWrapper2.getValue());
        assertEquals(Double.POSITIVE_INFINITY, (Double) stringWrapper1.getValue());

    }

    @Test
    void testDivideNull_() {

        nullWrapper1.divide(intWrapper2.getValue());
        assertInstanceOf(Integer.class, nullWrapper1.getValue());
        assertNull(nullWrapper2.getValue());
        assertEquals(0, nullWrapper1.getValue());
        assertEquals(7, intWrapper2.getValue());

        nullWrapper1.setValue(null);
        nullWrapper1.divide(doubleWrapper2.getValue());
        assertInstanceOf(Double.class, nullWrapper1.getValue());
        assertInstanceOf(Double.class, doubleWrapper2.getValue());
        assertEquals(0.0, nullWrapper1.getValue());
        assertEquals(7.0, doubleWrapper2.getValue());

        nullWrapper1.setValue(null);
        nullWrapper1.divide(stringWrapper2.getValue());
        assertInstanceOf(Double.class, nullWrapper1.getValue());
        assertInstanceOf(String.class, stringWrapper2.getValue());
        assertEquals(0.0, nullWrapper1.getValue());
        assertEquals("7.0e0", stringWrapper2.getValue());

        nullWrapper1.setValue(null);
        nullWrapper1.divide(nullWrapper2.getValue());
        assertInstanceOf(Double.class, nullWrapper1.getValue());
        assertNull(nullWrapper2.getValue());
        assertEquals(Double.NaN, nullWrapper1.getValue());

    }

    @Test
    void testDivideThrows() {

        ValueWrapper v1 = new ValueWrapper("gibberish");
        ValueWrapper v2 = new ValueWrapper("4");
        assertThrows(RuntimeException.class, () -> v1.divide(v2.getValue()));

        ValueWrapper v3 = new ValueWrapper("-7.0e0");
        ValueWrapper v4 = new ValueWrapper("more gibberish");
        assertThrows(RuntimeException.class, () -> v3.divide(v4.getValue()));

        ValueWrapper v5 = new ValueWrapper("even more gibberish");
        ValueWrapper v6 = new ValueWrapper("the most gibberish");
        assertThrows(RuntimeException.class, () -> v5.divide(v6.getValue()));

    }

    @Test
    void testDivideWithZero() {

        // divide(_, 0) -> +/- Infinity | NaN

        ValueWrapper v7 = new ValueWrapper(1);
        ValueWrapper v8 = new ValueWrapper(0);
        v7.divide(v8.getValue());
        assertInstanceOf(Double.class, v7.getValue());
        assertEquals(Double.POSITIVE_INFINITY, (Double) v7.getValue());

        ValueWrapper v9 = new ValueWrapper(-1);
        ValueWrapper v10 = new ValueWrapper(0);
        v9.divide(v10.getValue());
        assertInstanceOf(Double.class, v9.getValue());
        assertEquals(Double.NEGATIVE_INFINITY, (Double) v9.getValue());

        ValueWrapper v11 = new ValueWrapper(1.0);
        ValueWrapper v12 = new ValueWrapper(0);
        v11.divide(v12.getValue());
        assertInstanceOf(Double.class, v11.getValue());
        assertEquals(Double.POSITIVE_INFINITY, (Double) v11.getValue());

        ValueWrapper v13 = new ValueWrapper(-1.0);
        ValueWrapper v14 = new ValueWrapper(0);
        v13.divide(v14.getValue());
        assertInstanceOf(Double.class, v13.getValue());
        assertEquals(Double.NEGATIVE_INFINITY, (Double) v13.getValue());

        ValueWrapper v15 = new ValueWrapper("1");
        ValueWrapper v16 = new ValueWrapper(0);
        v15.divide(v16.getValue());
        assertInstanceOf(Double.class, v15.getValue());
        assertEquals(Double.POSITIVE_INFINITY, (Double) v15.getValue());

        ValueWrapper v17 = new ValueWrapper("-1");
        ValueWrapper v18 = new ValueWrapper(0);
        v17.divide(v18.getValue());
        assertInstanceOf(Double.class, v17.getValue());
        assertEquals(Double.NEGATIVE_INFINITY, (Double) v17.getValue());

        ValueWrapper v19 = new ValueWrapper("1.0e0");
        ValueWrapper v20 = new ValueWrapper(0);
        v19.divide(v20.getValue());
        assertInstanceOf(Double.class, v19.getValue());
        assertEquals(Double.POSITIVE_INFINITY, (Double) v19.getValue());

        ValueWrapper v21 = new ValueWrapper("-1.0e0");
        ValueWrapper v22 = new ValueWrapper(0);
        v21.divide(v22.getValue());
        assertInstanceOf(Double.class, v21.getValue());
        assertEquals(Double.NEGATIVE_INFINITY, (Double) v21.getValue());


        ValueWrapper v23 = new ValueWrapper(null);
        ValueWrapper v24 = new ValueWrapper(0);
        v23.divide(v24.getValue());
        assertInstanceOf(Double.class, v23.getValue());
        assertEquals(Double.NaN, (Double) v23.getValue());

        ValueWrapper v25 = new ValueWrapper(0);
        ValueWrapper v26 = new ValueWrapper(0);
        v25.divide(v26.getValue());
        assertInstanceOf(Double.class, v25.getValue());
        assertEquals(Double.NaN, (Double) v25.getValue());

        ValueWrapper v27 = new ValueWrapper("0");
        ValueWrapper v28 = new ValueWrapper(null);
        v27.divide(v28.getValue());
        assertInstanceOf(Double.class, v27.getValue());
        assertEquals(Double.NaN, (Double) v27.getValue());
    }

    @Test
    void testNumCompareInt_() {

        assertEquals(-1, intWrapper1.numCompare(intWrapper2.getValue()));
        assertEquals(-1, intWrapper1.numCompare(doubleWrapper2.getValue()));
        assertEquals(-1, intWrapper1.numCompare(stringWrapper2.getValue()));
        assertEquals(1, intWrapper1.numCompare(nullWrapper2.getValue()));

        assertEquals(0, intWrapper1.numCompare(doubleWrapper1.getValue()));
        assertEquals(0, intWrapper1.numCompare(stringWrapper1.getValue()));
    }

    @Test
    void testNumCompareDouble_() {

        assertEquals(-1, doubleWrapper1.numCompare(intWrapper2.getValue()));
        assertEquals(-1, doubleWrapper1.numCompare(doubleWrapper2.getValue()));
        assertEquals(-1, doubleWrapper1.numCompare(stringWrapper2.getValue()));
        assertEquals(1, doubleWrapper1.numCompare(nullWrapper2.getValue()));

        assertEquals(0, doubleWrapper1.numCompare(intWrapper1.getValue()));
        assertEquals(0, doubleWrapper1.numCompare(stringWrapper1.getValue()));
    }

    @Test
    void testNumCompareString_() {

        assertEquals(-1, stringWrapper1.numCompare(intWrapper2.getValue()));
        assertEquals(-1, stringWrapper1.numCompare(doubleWrapper2.getValue()));
        assertEquals(-1, stringWrapper1.numCompare(stringWrapper2.getValue()));
        assertEquals(1, stringWrapper1.numCompare(nullWrapper2.getValue()));

        assertEquals(0, stringWrapper1.numCompare(intWrapper1.getValue()));
        assertEquals(0, stringWrapper1.numCompare(doubleWrapper1.getValue()));
    }

    @Test
    void testNumCompareNull_() {

        assertEquals(-1, nullWrapper1.numCompare(intWrapper2.getValue()));
        assertEquals(-1, nullWrapper1.numCompare(doubleWrapper2.getValue()));
        assertEquals(-1, nullWrapper1.numCompare(stringWrapper2.getValue()));

        assertEquals(0, nullWrapper1.numCompare(nullWrapper2.getValue()));
    }

    @Test
    void testNumCompareThrows() {

        ValueWrapper v1 = new ValueWrapper("gibberish");
        ValueWrapper v2 = new ValueWrapper("4");
        assertThrows(RuntimeException.class, () -> v1.numCompare(v2.getValue()));

        ValueWrapper v3 = new ValueWrapper("-7.0e0");
        ValueWrapper v4 = new ValueWrapper("more gibberish");
        assertThrows(RuntimeException.class, () -> v3.numCompare(v4.getValue()));

        ValueWrapper v5 = new ValueWrapper("even more gibberish");
        ValueWrapper v6 = new ValueWrapper("the most gibberish");
        assertThrows(RuntimeException.class, () -> v5.numCompare(v6.getValue()));
    }
}
