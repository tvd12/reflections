package com.tvd12.reflections.testing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.tvd12.reflections.util.Iterables;

public class IterablesExtTest {

    // ---- any() ----

    @Test
    public void anyReturnsTrueWhenPredicateMatches() {
        Set<Integer> set = new HashSet<>(Arrays.asList(1, 2, 3));
        assertTrue(Iterables.any(set, n -> n.equals(2)));
    }

    @Test
    public void anyReturnsFalseWhenNoMatch() {
        Set<Integer> set = new HashSet<>(Arrays.asList(1, 2, 3));
        assertFalse(Iterables.any(set, n -> n.equals(99)));
    }

    @Test
    public void anyReturnsFalseOnEmptySet() {
        assertFalse(Iterables.any(new HashSet<>(), n -> true));
    }

    // ---- isEmpty() ----

    @Test
    public void isEmptyTrueForEmptyCollection() {
        assertTrue(Iterables.isEmpty(Collections.emptyList()));
    }

    @Test
    public void isEmptyFalseForNonEmptyCollection() {
        assertFalse(Iterables.isEmpty(Arrays.asList(1, 2, 3)));
    }

    @Test
    public void isEmptyTrueForNonCollectionIterable() {
        Iterable<String> empty = () -> Collections.<String>emptyIterator();
        assertTrue(Iterables.isEmpty(empty));
    }

    @Test
    public void isEmptyFalseForNonCollectionIterableWithElements() {
        Iterable<String> nonEmpty = () -> Arrays.asList("a", "b").iterator();
        assertFalse(Iterables.isEmpty(nonEmpty));
    }

    // ---- concat(Iterable[]) varargs ----

    @Test
    public void concatVarargsJoinsAllIterables() {
        List<String> a = Arrays.asList("x", "y");
        List<String> b = Arrays.asList("z");
        List<String> c = Arrays.asList("w", "v");

        @SuppressWarnings("unchecked")
        Iterable<String> result = Iterables.concat(a, b, c);

        List<String> collected = new ArrayList<>();
        for (String s : result) {
            collected.add(s);
        }
        assertEquals(Arrays.asList("x", "y", "z", "w", "v"), collected);
    }

    @Test
    public void concatVarargsWithEmptyIterablesSkipsThem() {
        List<String> empty = Collections.emptyList();
        List<String> values = Arrays.asList("a", "b");

        @SuppressWarnings("unchecked")
        Iterable<String> result = Iterables.concat(empty, values, empty);

        List<String> collected = new ArrayList<>();
        for (String s : result) {
            collected.add(s);
        }
        assertEquals(Arrays.asList("a", "b"), collected);
    }

    @Test
    public void concatVarargsCanBeIteratedMultipleTimes() {
        List<String> a = Arrays.asList("1");
        List<String> b = Arrays.asList("2");

        @SuppressWarnings("unchecked")
        Iterable<String> result = Iterables.concat(a, b);

        List<String> first = new ArrayList<>();
        for (String s : result) first.add(s);

        List<String> second = new ArrayList<>();
        for (String s : result) second.add(s);

        assertEquals(first, second);
    }
}
