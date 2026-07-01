package com.tvd12.reflections.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.ArrayList;

import org.junit.Test;

import com.tvd12.reflections.util.CollectSpliterators;
import com.tvd12.reflections.util.ImmutableEntry;
import com.tvd12.reflections.util.Iterables;
import com.tvd12.reflections.util.ObjectArrays;
import com.tvd12.reflections.util.UnmodifiableIterator;

public class UtilIteratorTest {

    // ---- ObjectArrays ----

    @Test
    public void concatArrays() {
        String[] a = {"x", "y"};
        String[] b = {"z"};
        String[] result = ObjectArrays.concat(a, b, String.class);
        assertEquals(3, result.length);
        assertEquals("x", result[0]);
        assertEquals("y", result[1]);
        assertEquals("z", result[2]);
    }

    @Test
    public void newArrayCreatesCorrectType() {
        Integer[] arr = ObjectArrays.newArray(Integer.class, 5);
        assertEquals(5, arr.length);
    }

    // ---- CollectSpliterators.filter ----

    @Test
    public void collectSpliteratorsFilterTryAdvance() {
        List<Integer> src = Arrays.asList(1, 2, 3, 4, 5);
        Spliterator<Integer> filtered = CollectSpliterators.filter(
            src.spliterator(), n -> n % 2 == 0
        );
        List<Integer> result = new ArrayList<>();
        while (filtered.tryAdvance(result::add)) {}
        assertEquals(Arrays.asList(2, 4), result);
    }

    @Test
    public void collectSpliteratorsFilterEstimateSize() {
        List<Integer> src = Arrays.asList(1, 2, 3, 4);
        Spliterator<Integer> filtered = CollectSpliterators.filter(
            src.spliterator(), n -> true
        );
        assertEquals(2L, filtered.estimateSize());
    }

    @Test
    public void collectSpliteratorsFilterCharacteristics() {
        List<Integer> src = Arrays.asList(1, 2, 3);
        Spliterator<Integer> filtered = CollectSpliterators.filter(
            src.spliterator(), n -> true
        );
        int chars = filtered.characteristics();
        assertTrue((chars & Spliterator.SIZED) == 0);
    }

    @Test
    public void collectSpliteratorsFilterTrySplitReturnsNull() {
        List<Integer> src = Arrays.asList(1, 2);
        // ArrayList spliterator trySplit on small list may return null
        Spliterator<Integer> filtered = CollectSpliterators.filter(
            src.spliterator(), n -> true
        );
        // just verify it doesn't throw
        filtered.trySplit();
    }

    @Test
    public void collectSpliteratorsViaIterablesFilter() {
        List<String> src = Arrays.asList("a", "bb", "ccc");
        Iterable<String> filtered = Iterables.filter(src, s -> s.length() > 1);
        List<String> result = new ArrayList<>();
        filtered.spliterator().forEachRemaining(result::add);
        assertEquals(Arrays.asList("bb", "ccc"), result);
    }

    // ---- ImmutableEntry ----

    @Test
    public void immutableEntryGetKeyAndValue() {
        ImmutableEntry<String, Integer> e = new ImmutableEntry<>("k", 42);
        assertEquals("k", e.getKey());
        assertEquals(Integer.valueOf(42), e.getValue());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void immutableEntrySetValueThrows() {
        new ImmutableEntry<>("k", 1).setValue(2);
    }

    // ---- UnmodifiableIterator ----

    @Test(expected = UnsupportedOperationException.class)
    public void unmodifiableIteratorRemoveThrows() {
        UnmodifiableIterator<String> iter = new UnmodifiableIterator<String>() {
            public boolean hasNext() { return false; }
            public String next()     { return null; }
        };
        iter.remove();
    }

}
