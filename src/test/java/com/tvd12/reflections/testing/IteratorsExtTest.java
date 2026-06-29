package com.tvd12.reflections.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.TreeSet;


import org.junit.Test;

import com.tvd12.reflections.util.CollectSpliterators;
import com.tvd12.reflections.util.Iterables;

public class IteratorsExtTest {

    // ---- Iterables.filter().forEach() ----

    @Test
    public void filterIterableForEachVisitsMatchingElements() {
        List<Integer> src = Arrays.asList(1, 2, 3, 4, 5);
        Iterable<Integer> filtered = Iterables.filter(src, n -> n % 2 == 0);
        List<Integer> result = new ArrayList<>();
        filtered.forEach(result::add);
        assertEquals(Arrays.asList(2, 4), result);
    }

    // ---- ConcatenatedIterator.remove ----

    @Test
    public void concatenatedIteratorRemoveDelegatesToCurrentBacking() {
        List<String> a = new ArrayList<>(Arrays.asList("x", "y"));
        List<String> b = new ArrayList<>(Arrays.asList("z"));

        @SuppressWarnings("unchecked")
        Iterable<String> concat = Iterables.concat(a, b);
        Iterator<String> it = concat.iterator();

        assertEquals("x", it.next());
        it.remove(); // should remove "x" from list a
        assertEquals(1, a.size());
        assertFalse(a.contains("x"));
    }

    // ---- CollectSpliterators.filter getComparator ----

    @Test
    public void filteredSpliteratorGetComparatorDelegatesToSource() {
        // A TreeSet spliterator has SORTED characteristic and a natural-order comparator
        TreeSet<Integer> sorted = new TreeSet<>(Arrays.asList(3, 1, 2));
        Spliterator<Integer> src = sorted.spliterator();
        Spliterator<Integer> filtered = CollectSpliterators.filter(src, n -> n > 0);
        // getComparator() delegates to the wrapped sorted spliterator
        Comparator<? super Integer> cmp = filtered.getComparator();
        // TreeSet natural order → comparator is null (natural ordering)
        assertNull(cmp);
    }
}
