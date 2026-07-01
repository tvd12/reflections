package com.tvd12.reflections.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.tvd12.reflections.util.Multimaps;
import com.tvd12.reflections.util.SetMultimap;
import com.tvd12.reflections.util.Sets;
import com.tvd12.reflections.util.SynchronizedSetMultimap;

import java.util.concurrent.ConcurrentHashMap;

public class SynchronizedSetMultimapTest {

    private SynchronizedSetMultimap<String, String> map;

    @Before
    public void setUp() {
        SetMultimap<String, String> inner = Multimaps.newSetMultimap(
            new HashMap<>(),
            () -> Sets.newSetFromMap(new ConcurrentHashMap<>())
        );
        map = new SynchronizedSetMultimap<>(inner);
        map.put("a", "1");
        map.put("a", "2");
        map.put("b", "3");
    }

    @Test
    public void values() {
        // values() returns one collection-bucket per distinct key (not flattened)
        Collection<String> values = map.values();
        assertEquals(2, values.size()); // 2 distinct keys → 2 buckets
    }

    @Test
    public void entries() {
        // entries() is flattened: one Entry per individual value
        Iterable<Map.Entry<String, String>> entries = map.entries();
        assertNotNull(entries);
        int count = 0;
        for (Map.Entry<String, String> e : entries) {
            assertNotNull(e.getKey());
            assertNotNull(e.getValue());
            count++;
        }
        assertEquals(3, count);
    }

    @Test
    public void asMap() {
        Map<String, Collection<String>> m = map.asMap();
        assertEquals(2, m.size());
        assertTrue(m.containsKey("a"));
        assertTrue(m.containsKey("b"));
        assertEquals(2, m.get("a").size());
    }

    @Test
    public void putAllFromMultimap() {
        SetMultimap<String, String> other = Multimaps.newSetMultimap(
            new HashMap<>(),
            () -> Sets.newSetFromMap(new ConcurrentHashMap<>())
        );
        other.put("c", "4");
        assertTrue(map.putAll(other));
        assertTrue(map.get("c").contains("4"));
    }

    @Test
    public void sizeAndIsEmpty() {
        assertEquals(2, map.size()); // size() = number of distinct keys
        assertFalse(map.isEmpty());
    }

    @Test
    public void keySet() {
        assertEquals(2, map.keySet().size());
    }
}
