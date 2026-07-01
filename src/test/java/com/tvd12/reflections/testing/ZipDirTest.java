package com.tvd12.reflections.testing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.jar.JarFile;

import org.junit.Test;

import com.tvd12.reflections.util.ClasspathHelper;
import com.tvd12.reflections.vfs.ZipDir;

public class ZipDirTest {

    private JarFile openTestJar() throws Exception {
        // Use a JAR from the test classpath — jarWithManifest.jar is bundled in test resources
        URL jarUrl = ZipDirTest.class.getClassLoader().getResource("jarWithManifest.jar");
        assertNotNull("jarWithManifest.jar must exist on test classpath", jarUrl);
        return new JarFile(jarUrl.getFile());
    }

    @Test
    public void getPathReturnsJarName() throws Exception {
        try (JarFile jar = openTestJar()) {
            ZipDir dir = new ZipDir(jar);
            String path = dir.getPath();
            assertNotNull(path);
            assertTrue(path.endsWith(".jar"));
        }
    }

    @Test
    public void toStringReturnsJarName() throws Exception {
        try (JarFile jar = openTestJar()) {
            ZipDir dir = new ZipDir(jar);
            String str = dir.toString();
            assertNotNull(str);
            assertTrue(str.endsWith(".jar"));
        }
    }

    @Test
    public void closeDoesNotThrow() throws Exception {
        JarFile jar = openTestJar();
        ZipDir dir = new ZipDir(jar);
        dir.close(); // must not throw
    }

    @Test
    public void getFilesIteratesEntries() throws Exception {
        try (JarFile jar = openTestJar()) {
            ZipDir dir = new ZipDir(jar);
            int count = 0;
            for (com.tvd12.reflections.vfs.Vfs.File f : dir.getFiles()) {
                assertNotNull(f.getName());
                count++;
            }
            assertTrue("Expected at least one file in JAR", count > 0);
        }
    }
}
