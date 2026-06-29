package com.tvd12.reflections.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.tvd12.reflections.scanners.AbstractScanner;
import com.tvd12.reflections.scanners.SubTypesScanner;
import com.tvd12.reflections.scanners.TypeAnnotationsScanner;
import com.tvd12.reflections.util.FilterBuilder;

public class AbstractScannerTest {

    @Test
    public void acceptResultDefaultAcceptsAll() {
        SubTypesScanner scanner = new SubTypesScanner();
        assertTrue(scanner.acceptResult("com.example.Foo"));
        assertTrue(scanner.acceptResult("anything"));
    }

    @Test
    public void acceptResultReturnsFalseForNull() {
        SubTypesScanner scanner = new SubTypesScanner();
        assertFalse(scanner.acceptResult(null));
    }

    @Test
    public void filterResultsByAppliesFilter() {
        SubTypesScanner scanner = new SubTypesScanner();
        scanner.filterResultsBy(new FilterBuilder().include("com\\.example\\..*"));
        assertTrue(scanner.acceptResult("com.example.Foo"));
        assertFalse(scanner.acceptResult("org.other.Bar"));
    }

    @Test
    public void setAndGetResultFilter() {
        SubTypesScanner scanner = new SubTypesScanner();
        FilterBuilder filter = new FilterBuilder().include(".*");
        scanner.setResultFilter(filter);
        assertNotNull(scanner.getResultFilter());
    }

    @Test
    public void equalsAndHashCodeSameClass() {
        SubTypesScanner a = new SubTypesScanner();
        SubTypesScanner b = new SubTypesScanner();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void notEqualsToDifferentScannerClass() {
        AbstractScanner a = new SubTypesScanner();
        AbstractScanner b = new TypeAnnotationsScanner();
        assertFalse(a.equals(b));
    }

    @Test
    public void notEqualsToNull() {
        assertFalse(new SubTypesScanner().equals(null));
    }

    @Test
    public void equalsToSelf() {
        SubTypesScanner s = new SubTypesScanner();
        assertTrue(s.equals(s));
    }
}
