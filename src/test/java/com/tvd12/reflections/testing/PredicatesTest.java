package com.tvd12.reflections.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Test;

import com.tvd12.reflections.util.Predicates;

public class PredicatesTest {

    // ---- InPredicate ----

    @Test
    public void inPredicateMatches() {
        List<String> target = Arrays.asList("a", "b", "c");
        Predicate<String> pred = Predicates.in(target);
        assertTrue(pred.test("a"));
        assertFalse(pred.test("z"));
    }

    @Test
    public void inPredicateEquals() {
        List<String> target = Arrays.asList("x", "y");
        Predicate<String> p1 = Predicates.in(target);
        Predicate<String> p2 = Predicates.in(Arrays.asList("x", "y"));
        assertEquals(p1, p2);
    }

    @Test
    public void inPredicateNotEqualsOtherType() {
        Predicate<String> pred = Predicates.in(Arrays.asList("a"));
        assertNotEquals(pred, "notAPredicate");
    }

    @Test
    public void inPredicateHashCode() {
        List<String> target = Arrays.asList("a", "b");
        Predicate<String> pred = Predicates.in(target);
        assertEquals(target.hashCode(), pred.hashCode());
    }

    @Test
    public void inPredicateToString() {
        List<String> target = Arrays.asList("q");
        String str = Predicates.in(target).toString();
        assertTrue(str.contains("in"));
        assertTrue(str.contains("q"));
    }

    // ---- NotPredicate ----

    @Test
    public void notPredicateNegates() {
        Predicate<String> notEmpty = Predicates.not(s -> s.isEmpty());
        assertTrue(notEmpty.test("hello"));
        assertFalse(notEmpty.test(""));
    }

    @Test
    public void notPredicateEquals() {
        Predicate<String> base = s -> s.startsWith("a");
        // Two NotPredicates wrapping equal predicates should be equal themselves
        // if the wrapped predicate supports equals — lambda predicates don't,
        // so just verify it doesn't crash and is symmetric
        Predicate<String> p1 = Predicates.not(base);
        Predicate<String> p2 = Predicates.not(base);
        // Same wrapped reference → equal
        assertEquals(p1, p2);
    }

    @Test
    public void notPredicateNotEqualsOtherType() {
        Predicate<String> pred = Predicates.not(s -> true);
        assertNotEquals(pred, "notAPredicate");
    }

    @Test
    public void notPredicateHashCode() {
        Predicate<String> base = s -> true;
        Predicate<String> pred = Predicates.not(base);
        // ~hashCode of base; just verify it doesn't throw
        int h = pred.hashCode();
        assertEquals(~base.hashCode(), h);
    }

    @Test
    public void notPredicateToString() {
        Predicate<String> pred = Predicates.not(s -> true);
        String str = pred.toString();
        assertTrue(str.contains("not"));
    }
}
