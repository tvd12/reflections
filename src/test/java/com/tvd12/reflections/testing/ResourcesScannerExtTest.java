package com.tvd12.reflections.testing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.tvd12.reflections.scanners.ResourcesScanner;

public class ResourcesScannerExtTest {

    private final ResourcesScanner scanner = new ResourcesScanner();

    @Test
    public void acceptsInputRejectsClassFiles() {
        assertFalse(scanner.acceptsInput("com/example/Foo.class"));
    }

    @Test
    public void acceptsInputAcceptsNonClassFiles() {
        assertTrue(scanner.acceptsInput("META-INF/MANIFEST.MF"));
        assertTrue(scanner.acceptsInput("config/app.xml"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void scanObjectThrowsUnsupportedOperationException() {
        scanner.scan(new Object());
    }
}
