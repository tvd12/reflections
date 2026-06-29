package com.tvd12.reflections.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;

public class IteratorInternalsTest {

    // ---- AbstractIndexedListIterator ----

    private AbstractIndexedListIterator<String> makeIter(int size, int pos) {
        return new AbstractIndexedListIterator<String>(size, pos) {
            protected String get(int i) { return "item" + i; }
        };
    }

    @Test
    public void forwardTraversal() {
        AbstractIndexedListIterator<String> iter = makeIter(3, 0);
        assertTrue(iter.hasNext());
        assertEquals(0, iter.nextIndex());
        assertEquals("item0", iter.next());
        assertEquals("item1", iter.next());
        assertEquals("item2", iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void backwardTraversal() {
        AbstractIndexedListIterator<String> iter = makeIter(3, 3);
        assertTrue(iter.hasPrevious());
        assertEquals(2, iter.previousIndex());
        assertEquals("item2", iter.previous());
        assertEquals("item1", iter.previous());
        assertEquals("item0", iter.previous());
        assertFalse(iter.hasPrevious());
    }

    @Test(expected = NoSuchElementException.class)
    public void nextBeyondEndThrows() {
        makeIter(1, 1).next();
    }

    @Test(expected = NoSuchElementException.class)
    public void previousBeforeStartThrows() {
        makeIter(1, 0).previous();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addThrows() {
        makeIter(2, 0).add("x");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setThrows() {
        makeIter(2, 0).set("x");
    }

    // ---- TransformedIterator ----

    @Test
    public void transformedIteratorNextAndHasNext() {
        List<Integer> src = Arrays.asList(1, 2, 3);
        Iterator<String> iter = new TransformedIterator<Integer, String>(src.iterator()) {
            String transform(Integer from) { return "v" + from; }
        };
        assertTrue(iter.hasNext());
        assertEquals("v1", iter.next());
        assertEquals("v2", iter.next());
        assertEquals("v3", iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void transformedIteratorRemoveDelegatesToBacking() {
        List<Integer> src = new ArrayList<>(Arrays.asList(1, 2, 3));
        Iterator<String> iter = new TransformedIterator<Integer, String>(src.iterator()) {
            String transform(Integer from) { return String.valueOf(from); }
        };
        iter.next();
        iter.remove();
        assertEquals(2, src.size());
    }
}
