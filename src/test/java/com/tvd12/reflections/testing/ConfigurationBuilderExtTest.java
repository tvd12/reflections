package com.tvd12.reflections.testing;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.tvd12.reflections.adapters.JavaReflectionAdapter;
import com.tvd12.reflections.util.ConfigurationBuilder;

public class ConfigurationBuilderExtTest {

    @Test
    public void addClassLoadersWithVarArgs() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        ConfigurationBuilder cb = new ConfigurationBuilder()
            .forPackages("com.tvd12.reflections.testing")
            .addClassLoaders(cl);
        assertNotNull(cb);
    }

    @Test
    public void addClassLoadersWithCollection() {
        java.util.List<ClassLoader> loaders = java.util.Arrays.asList(
            Thread.currentThread().getContextClassLoader()
        );
        ConfigurationBuilder cb = new ConfigurationBuilder()
            .forPackages("com.tvd12.reflections.testing")
            .addClassLoaders(loaders);
        assertNotNull(cb);
    }

    @Test
    public void setMetadataAdapterCustomAdapter() {
        ConfigurationBuilder cb = new ConfigurationBuilder()
            .forPackages("com.tvd12.reflections.testing")
            .setMetadataAdapter(new JavaReflectionAdapter());
        assertNotNull(cb);
        assertTrue(cb.getMetadataAdapter() instanceof JavaReflectionAdapter);
    }
}
