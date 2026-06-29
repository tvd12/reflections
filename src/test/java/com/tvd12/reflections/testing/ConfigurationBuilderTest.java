package com.tvd12.reflections.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import com.tvd12.reflections.scanners.FieldAnnotationsScanner;
import com.tvd12.reflections.scanners.SubTypesScanner;
import com.tvd12.reflections.scanners.TypeAnnotationsScanner;
import com.tvd12.reflections.serializers.XmlSerializer;
import com.tvd12.reflections.util.ClasspathHelper;
import com.tvd12.reflections.util.ConfigurationBuilder;
import com.tvd12.reflections.util.FilterBuilder;

public class ConfigurationBuilderTest {

    // ---- setScanners / addScanners ----

    @Test
    public void setScannersReplacesDefaults() {
        ConfigurationBuilder cb = new ConfigurationBuilder()
            .setScanners(new FieldAnnotationsScanner());
        assertEquals(1, cb.getScanners().size());
        assertTrue(cb.getScanners().stream().anyMatch(s -> s instanceof FieldAnnotationsScanner));
    }

    @Test
    public void addScannersAppendsToExisting() {
        ConfigurationBuilder cb = new ConfigurationBuilder()
            .addScanners(new FieldAnnotationsScanner());
        // default has TypeAnnotations + SubTypes, now adds one more
        assertTrue(cb.getScanners().size() >= 3);
    }

    // ---- setUrls / addUrls ----

    @Test
    public void setUrlsVarargs() throws MalformedURLException {
        URL url = new URL("file:/tmp/test.jar");
        ConfigurationBuilder cb = new ConfigurationBuilder().setUrls(url);
        assertTrue(cb.getUrls().contains(url));
    }

    @Test
    public void addUrlsCollection() throws MalformedURLException {
        URL url = new URL("file:/tmp/test.jar");
        ConfigurationBuilder cb = new ConfigurationBuilder()
            .addUrls(Arrays.asList(url));
        assertTrue(cb.getUrls().contains(url));
    }

    @Test
    public void addUrlsVarargs() throws MalformedURLException {
        URL url1 = new URL("file:/tmp/a.jar");
        URL url2 = new URL("file:/tmp/b.jar");
        ConfigurationBuilder cb = new ConfigurationBuilder().addUrls(url1, url2);
        assertTrue(cb.getUrls().contains(url1));
        assertTrue(cb.getUrls().contains(url2));
    }

    // ---- forPackages ----

    @Test
    public void forPackagesAddsUrls() {
        ConfigurationBuilder cb = new ConfigurationBuilder()
            .forPackages("com.tvd12.reflections");
        assertFalse(cb.getUrls().isEmpty());
    }

    // ---- filterInputsBy / setInputsFilter ----

    @Test
    public void filterInputsBy() {
        FilterBuilder filter = new FilterBuilder().include("com\\.example\\..*");
        ConfigurationBuilder cb = new ConfigurationBuilder().filterInputsBy(filter);
        assertNotNull(cb.getInputsFilter());
        assertTrue(cb.getInputsFilter().test("com.example.Foo"));
        assertFalse(cb.getInputsFilter().test("org.other.Bar"));
    }

    @Test
    public void setInputsFilter() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setInputsFilter(s -> s.startsWith("com"));
        assertNotNull(cb.getInputsFilter());
    }

    @Test
    public void setInputsFilterNull() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setInputsFilter(null);
        assertNull(cb.getInputsFilter());
    }

    // ---- executor ----

    @Test
    public void useParallelExecutorSetsExecutor() {
        ConfigurationBuilder cb = new ConfigurationBuilder().useParallelExecutor();
        assertNotNull(cb.getExecutorService());
        cb.getExecutorService().shutdownNow();
    }

    @Test
    public void useParallelExecutorWithThreadCount() {
        ConfigurationBuilder cb = new ConfigurationBuilder().useParallelExecutor(2);
        assertNotNull(cb.getExecutorService());
        cb.getExecutorService().shutdownNow();
    }

    @Test
    public void setExecutorService() {
        ExecutorService es = Executors.newSingleThreadExecutor();
        ConfigurationBuilder cb = new ConfigurationBuilder().setExecutorService(es);
        assertEquals(es, cb.getExecutorService());
        es.shutdownNow();
    }

    // ---- serializer ----

    @Test
    public void setSerializer() {
        XmlSerializer xs = new XmlSerializer();
        ConfigurationBuilder cb = new ConfigurationBuilder().setSerializer(xs);
        assertEquals(xs, cb.getSerializer());
    }

    // ---- expandSuperTypes ----

    @Test
    public void shouldExpandSuperTypesDefaultTrue() {
        assertTrue(new ConfigurationBuilder().shouldExpandSuperTypes());
    }

    @Test
    public void setExpandSuperTypesFalse() {
        ConfigurationBuilder cb = new ConfigurationBuilder().setExpandSuperTypes(false);
        assertFalse(cb.shouldExpandSuperTypes());
    }

    // ---- classLoaders ----

    @Test
    public void addClassLoader() {
        ClassLoader cl = new ClassLoader(null) {};
        ConfigurationBuilder cb = new ConfigurationBuilder().addClassLoader(cl);
        assertTrue(Arrays.asList(cb.getClassLoaders()).contains(cl));
    }

    @Test
    public void addClassLoadersVarargs() {
        ClassLoader cl1 = new ClassLoader(null) {};
        ClassLoader cl2 = new ClassLoader(null) {};
        ConfigurationBuilder cb = new ConfigurationBuilder().addClassLoaders(cl1, cl2);
        assertTrue(Arrays.asList(cb.getClassLoaders()).contains(cl1));
        assertTrue(Arrays.asList(cb.getClassLoaders()).contains(cl2));
    }

    @Test
    public void setClassLoaders() {
        ClassLoader cl = new ClassLoader(null) {};
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setClassLoaders(new ClassLoader[]{cl});
        assertEquals(1, cb.getClassLoaders().length);
    }

    // ---- build(Object...) ----

    @Test
    public void buildFromPackageString() {
        ConfigurationBuilder cb = ConfigurationBuilder.build("com.tvd12.reflections");
        assertFalse(cb.getUrls().isEmpty());
        assertNotNull(cb.getInputsFilter());
    }

    @Test
    public void buildFromClass() {
        ConfigurationBuilder cb = ConfigurationBuilder.build(TestModel.class);
        assertFalse(cb.getUrls().isEmpty());
    }

    @Test
    public void buildFromScanner() {
        ConfigurationBuilder cb = ConfigurationBuilder.build(
            ClasspathHelper.forClass(TestModel.class),
            new SubTypesScanner()
        );
        assertTrue(cb.getScanners().stream().anyMatch(s -> s instanceof SubTypesScanner));
        assertFalse(cb.getScanners().stream().anyMatch(s -> s instanceof TypeAnnotationsScanner));
    }

    @Test
    public void buildFromUrl() throws MalformedURLException {
        URL url = ClasspathHelper.forClass(TestModel.class);
        ConfigurationBuilder cb = ConfigurationBuilder.build(url);
        assertTrue(cb.getUrls().contains(url));
    }

    @Test
    public void buildFromNullDoesNotThrow() {
        ConfigurationBuilder cb = ConfigurationBuilder.build((Object) null);
        assertNotNull(cb);
    }
}
