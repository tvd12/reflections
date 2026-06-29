package com.tvd12.reflections.testing;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.tvd12.reflections.util.FilterBuilder;

public class FilterBuilderExtTest {

    @Test
    public void toStringContainsIncludeAndExclude() {
        FilterBuilder fb = new FilterBuilder()
            .include("com\\.example\\..*")
            .exclude("com\\.example\\.internal\\..*");
        String str = fb.toString();
        assertTrue(str.contains("+"));
        assertTrue(str.contains("-"));
    }

    @Test
    public void includeToStringHasPlusPrefix() {
        FilterBuilder.Include inc = new FilterBuilder.Include("foo\\..*");
        assertTrue(inc.toString().startsWith("+"));
        assertTrue(inc.toString().contains("foo"));
    }

    @Test
    public void excludeToStringHasMinusPrefix() {
        FilterBuilder.Exclude exc = new FilterBuilder.Exclude("bar\\..*");
        assertTrue(exc.toString().startsWith("-"));
        assertTrue(exc.toString().contains("bar"));
    }

    @Test
    public void matcherToStringReturnsPattern() {
        FilterBuilder.Include inc = new FilterBuilder.Include("hello\\..*");
        // Matcher.toString is the pattern string without prefix
        String superStr = inc.toString();
        assertTrue(superStr.contains("hello"));
    }

    @Test
    public void filterBuilderEmptyToStringIsEmpty() {
        FilterBuilder fb = new FilterBuilder();
        assertTrue(fb.toString().isEmpty());
    }
}
