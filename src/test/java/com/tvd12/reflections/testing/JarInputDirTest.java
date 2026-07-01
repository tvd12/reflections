package com.tvd12.reflections.testing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.tvd12.reflections.vfs.JarInputDir;
import com.tvd12.reflections.vfs.Vfs;

public class JarInputDirTest {

    private URL jarUrl() {
        URL url = JarInputDirTest.class.getClassLoader().getResource("jarWithManifest.jar");
        assertNotNull("jarWithManifest.jar must be on the test classpath", url);
        return url;
    }

    @Test
    public void getPathReturnsUrlPath() {
        URL url = jarUrl();
        JarInputDir dir = new JarInputDir(url);
        String path = dir.getPath();
        assertNotNull(path);
        assertTrue(path.endsWith(".jar"));
    }

    @Test
    public void closeDoesNotThrowWhenNeverOpened() {
        JarInputDir dir = new JarInputDir(jarUrl());
        dir.close(); // jarInputStream is null — Utils.close must handle this gracefully
    }

    @Test
    public void getFilesIteratesJarEntries() {
        JarInputDir dir = new JarInputDir(jarUrl());
        List<String> names = new ArrayList<>();
        for (Vfs.File f : dir.getFiles()) {
            names.add(f.getName());
            // also exercise getRelativePath
            assertNotNull(f.getRelativePath());
        }
        dir.close();
        assertFalse("Expected at least one file in JAR", names.isEmpty());
    }
}
