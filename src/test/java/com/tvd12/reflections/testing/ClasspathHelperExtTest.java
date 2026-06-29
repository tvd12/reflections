package com.tvd12.reflections.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.tvd12.reflections.util.ClasspathHelper;

public class ClasspathHelperExtTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

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

    // ---- forWebInfLib ----

    @Test
    public void forWebInfLibReturnsEmptyWhenResourcePathsNull() {
        ServletContext context = servletContext(null, java.util.Collections.emptyMap());

        Collection<URL> urls = ClasspathHelper.forWebInfLib(context);

        assertTrue(urls.isEmpty());
    }

    @Test
    public void forWebInfLibReturnsDistinctUrlsInServletContextOrder() throws Exception {
        URL first = new URL("file:/app/WEB-INF/lib/first.jar");
        URL second = new URL("file:/app/WEB-INF/lib/second.jar");
        Set<String> paths = new LinkedHashSet<>(
            Arrays.asList(
                "/WEB-INF/lib/first.jar",
                "/WEB-INF/lib/second.jar",
                "/WEB-INF/lib/first-copy.jar"
            )
        );
        java.util.Map<String, URL> resources = new java.util.HashMap<>();
        resources.put("/WEB-INF/lib/first.jar", first);
        resources.put("/WEB-INF/lib/second.jar", second);
        resources.put("/WEB-INF/lib/first-copy.jar", first);
        ServletContext context = servletContext(paths, resources);

        Collection<URL> urls = ClasspathHelper.forWebInfLib(context);

        assertEquals(Arrays.asList(first, second), new java.util.ArrayList<>(urls));
    }

    @Test
    public void forWebInfLibSkipsMalformedResource() throws Exception {
        URL valid = new URL("file:/app/WEB-INF/lib/valid.jar");
        Set<String> paths = new LinkedHashSet<>(
            Arrays.asList("/WEB-INF/lib/broken.jar", "/WEB-INF/lib/valid.jar")
        );
        java.util.Map<String, URL> resources = new java.util.HashMap<>();
        resources.put("/WEB-INF/lib/valid.jar", valid);
        ServletContext context = servletContext(paths, resources);

        Collection<URL> urls = ClasspathHelper.forWebInfLib(context);

        assertEquals(java.util.Collections.singletonList(valid), new java.util.ArrayList<>(urls));
    }

    // ---- forWebInfClasses ----

    @Test
    public void forWebInfClassesReturnsExistingRealPathUrl() throws Exception {
        File classes = folder.newFolder("WEB-INF", "classes");
        ServletContext context = servletContext(
            null,
            java.util.Collections.emptyMap(),
            classes.getPath()
        );

        URL url = ClasspathHelper.forWebInfClasses(context);

        assertEquals(classes.toURI().toURL(), url);
    }

    @Test
    public void forWebInfClassesReturnsNullWhenRealPathDoesNotExist() {
        ServletContext context = servletContext(
            null,
            java.util.Collections.emptyMap(),
            new File(folder.getRoot(), "missing").getPath()
        );

        assertNull(ClasspathHelper.forWebInfClasses(context));
    }

    @Test
    public void forWebInfClassesFallsBackToServletResourceWhenRealPathNull()
            throws Exception {
        URL expected = new URL("file:/app/WEB-INF/classes/");
        java.util.Map<String, URL> resources = new java.util.HashMap<>();
        resources.put("/WEB-INF/classes", expected);
        ServletContext context = servletContext(null, resources, null);

        assertEquals(expected, ClasspathHelper.forWebInfClasses(context));
    }

    @Test
    public void forWebInfClassesReturnsNullWhenServletResourceMalformed() {
        ServletContext context = servletContext(
            null,
            java.util.Collections.emptyMap(),
            null
        );

        assertNull(ClasspathHelper.forWebInfClasses(context));
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

    private static ServletContext servletContext(
        Set<String> resourcePaths,
        Map<String, URL> resources
    ) {
        return servletContext(resourcePaths, resources, null);
    }

    private static ServletContext servletContext(
        Set<String> resourcePaths,
        Map<String, URL> resources,
        String realPath
    ) {
        return (ServletContext) Proxy.newProxyInstance(
            ServletContext.class.getClassLoader(),
            new Class[] { ServletContext.class },
            (proxy, method, args) -> {
                if ("getRealPath".equals(method.getName())) {
                    return realPath;
                }
                if ("getResourcePaths".equals(method.getName())) {
                    return resourcePaths;
                }
                if ("getResource".equals(method.getName())) {
                    URL url = resources.get(args[0]);
                    if (url == null) {
                        throw new MalformedURLException(String.valueOf(args[0]));
                    }
                    return url;
                }
                throw new UnsupportedOperationException(method.getName());
            }
        );
    }
}
