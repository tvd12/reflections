package com.tvd12.reflections.testing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.tvd12.reflections.Reflections;
import com.tvd12.reflections.scanners.TypeElementsScanner;
import com.tvd12.reflections.util.ClasspathHelper;
import com.tvd12.reflections.util.ConfigurationBuilder;

public class TypeElementsScannerTest {

    // ---- fluent setters return this ----

    @Test
    public void includeFieldsNoArgReturnsSelf() {
        TypeElementsScanner s = new TypeElementsScanner();
        assertSame(s, s.includeFields());
    }

    @Test
    public void includeFieldsBooleanReturnsSelf() {
        TypeElementsScanner s = new TypeElementsScanner();
        assertSame(s, s.includeFields(false));
    }

    @Test
    public void includeMethodsNoArgReturnsSelf() {
        TypeElementsScanner s = new TypeElementsScanner();
        assertSame(s, s.includeMethods());
    }

    @Test
    public void includeMethodsBooleanReturnsSelf() {
        TypeElementsScanner s = new TypeElementsScanner();
        assertSame(s, s.includeMethods(false));
    }

    @Test
    public void includeAnnotationsNoArgReturnsSelf() {
        TypeElementsScanner s = new TypeElementsScanner();
        assertSame(s, s.includeAnnotations());
    }

    @Test
    public void includeAnnotationsBooleanReturnsSelf() {
        TypeElementsScanner s = new TypeElementsScanner();
        assertSame(s, s.includeAnnotations(false));
    }

    @Test
    public void publicOnlyNoArgReturnsSelf() {
        TypeElementsScanner s = new TypeElementsScanner();
        assertSame(s, s.publicOnly());
    }

    @Test
    public void publicOnlyBooleanReturnsSelf() {
        TypeElementsScanner s = new TypeElementsScanner();
        assertSame(s, s.publicOnly(false));
    }

    // ---- scan behavior ----

    @Test
    public void scanStoresClassElements() {
        Reflections ref = new Reflections(new ConfigurationBuilder()
            .setUrls(ClasspathHelper.forClass(TestModel.class))
            .setScanners(new TypeElementsScanner().publicOnly(false)));
        // TestModel should be indexed
        assertTrue(ref.getStore().keySet().contains("TypeElementsScanner"));
    }

    @Test
    public void scanWithFieldsDisabled() {
        TypeElementsScanner scanner = new TypeElementsScanner()
            .includeFields(false)
            .includeMethods(false)
            .includeAnnotations(false)
            .publicOnly(false);
        Reflections ref = new Reflections(new ConfigurationBuilder()
            .setUrls(ClasspathHelper.forClass(TestModel.class))
            .setScanners(scanner));
        assertFalse(ref.getStore().keySet().isEmpty());
    }
}
