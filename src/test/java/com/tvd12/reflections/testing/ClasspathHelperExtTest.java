package com.tvd12.reflections.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.Collection;

import org.junit.Test;

import com.tvd12.reflections.util.ClasspathHelper;

public class ClasspathHelperExtTest {

    // ---- contextClassLoader / staticClassLoader ----

    @Test
    public void contextClassLoaderReturnsCurrentThread() {
        assertEquals(
            Thread.currentThread().getContextClassLoader(),
            ClasspathHelper.contextClassLoader()
        );
    }

    @Test
    public void staticClassLoaderReturnsReflectionsLoader() {
        assertNotNull(ClasspathHelper.staticClassLoader());
    }

    // ---- classLoaders ----

    @Test
    public void classLoadersWithExplicitLoader() {
        ClassLoader cl = new ClassLoader(null) {};
        ClassLoader[] result = ClasspathHelper.classLoaders(cl);
        assertEquals(1, result.length);
        assertEquals(cl, result[0]);
    }

    @Test
    public void classLoadersWithNullReturnsDefaults() {
        ClassLoader[] result = ClasspathHelper.classLoaders((ClassLoader[]) null);
        assertTrue(result.length >= 1);
    }

    // ---- forPackage ----

    @Test
    public void forPackageFindsReflectionsPackage() {
        Collection<URL> urls = ClasspathHelper.forPackage("com.tvd12.reflections");
        assertFalse(urls.isEmpty());
    }

    @Test
    public void forPackageUnknownReturnsEmpty() {
        Collection<URL> urls = ClasspathHelper.forPackage("com.nonexistent.pkg.xyz");
        assertTrue(urls.isEmpty());
    }

    // ---- forClass ----

    @Test
    public void forClassReturnsUrlContainingClass() {
        URL url = ClasspathHelper.forClass(String.class);
        assertNotNull(url);
    }

    @Test
    public void forClassReturnsUrlForTestClass() {
        URL url = ClasspathHelper.forClass(TestModel.class);
        assertNotNull(url);
    }

    // ---- forJavaClassPath ----

    @Test
    public void forJavaClassPathReturnsNonEmpty() {
        Collection<URL> urls = ClasspathHelper.forJavaClassPath();
        assertFalse(urls.isEmpty());
    }

    // ---- forManifest ----

    @Test
    public void forManifestWithNoArgsReturnsUrls() {
        Collection<URL> urls = ClasspathHelper.forManifest();
        assertFalse(urls.isEmpty());
    }

    @Test
    public void forManifestWithUrlAlwaysContainsInput() throws Exception {
        URL url = ClasspathHelper.forClass(TestModel.class);
        Collection<URL> urls = ClasspathHelper.forManifest(url);
        assertTrue(urls.contains(url));
    }

    @Test
    public void forManifestWithIterable() throws Exception {
        URL url = ClasspathHelper.forClass(TestModel.class);
        java.util.List<URL> list = java.util.Arrays.asList(url);
        Collection<URL> urls = ClasspathHelper.forManifest(list);
        assertTrue(urls.contains(url));
    }

    // ---- cleanPath ----

    @Test
    public void cleanPathStripsJarProtocol() throws Exception {
        URL url = new URL("jar:file:/path/to/foo.jar!/com/example/");
        String path = ClasspathHelper.cleanPath(url);
        assertFalse(path.startsWith("jar:"));
        assertFalse(path.startsWith("file:"));
    }

    @Test
    public void cleanPathStripsFileProtocol() throws Exception {
        URL url = new URL("file:/path/to/dir/");
        String path = ClasspathHelper.cleanPath(url);
        assertFalse(path.startsWith("file:"));
        assertTrue(path.startsWith("/"));
    }

    @Test
    public void cleanPathStripsTrailingExclamation() throws Exception {
        URL url = new URL("jar:file:/path/to/foo.jar!/");
        String path = ClasspathHelper.cleanPath(url);
        assertFalse(path.endsWith("!/"));
        assertTrue(path.endsWith(".jar/"));
    }

    // ---- forResource ----

    @Test
    public void forResourceFindsManifest() {
        Collection<URL> urls = ClasspathHelper.forResource("META-INF/MANIFEST.MF");
        assertFalse(urls.isEmpty());
    }
}
