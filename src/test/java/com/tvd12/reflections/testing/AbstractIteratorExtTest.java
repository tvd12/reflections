package com.tvd12.reflections.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;

import com.tvd12.reflections.util.AbstractIterator;

public class AbstractIteratorExtTest {

    private AbstractIterator<String> iteratorOf(String... values) {
        Iterator<String> src = Arrays.asList(values).iterator();
        return new AbstractIterator<String>() {
            protected String computeNext() {
                return src.hasNext() ? src.next() : endOfData();
            }
        };
    }

    @Test
    public void hasNextReturnsTrueWhenElementsExist() {
        AbstractIterator<String> it = iteratorOf("a", "b");
        assertTrue(it.hasNext());
        assertTrue(it.hasNext()); // idempotent
    }

    @Test
    public void hasNextReturnsFalseWhenEmpty() {
        assertFalse(iteratorOf().hasNext());
    }

    @Test
    public void nextReturnsElementsInOrder() {
        AbstractIterator<String> it = iteratorOf("x", "y", "z");
        assertEquals("x", it.next());
        assertEquals("y", it.next());
        assertEquals("z", it.next());
    }

    @Test(expected = NoSuchElementException.class)
    public void nextThrowsWhenExhausted() {
        iteratorOf().next();
    }

    @Test
    public void peekReturnsCurrentWithoutAdvancing() {
        AbstractIterator<String> it = iteratorOf("hello", "world");
        assertEquals("hello", it.peek());
        assertEquals("hello", it.peek()); // does not advance
        assertEquals("hello", it.next()); // still same element
        assertEquals("world", it.peek());
    }

    @Test(expected = NoSuchElementException.class)
    public void peekThrowsWhenExhausted() {
        iteratorOf().peek();
    }

    @Test
    public void fullTraversalViaHasNextAndNext() {
        AbstractIterator<String> it = iteratorOf("a", "b", "c");
        List<String> result = new java.util.ArrayList<>();
        while (it.hasNext()) {
            result.add(it.next());
        }
        assertEquals(Arrays.asList("a", "b", "c"), result);
        assertFalse(it.hasNext());
    }
}
