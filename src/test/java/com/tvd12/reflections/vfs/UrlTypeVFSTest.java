package com.tvd12.reflections.vfs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;

import com.tvd12.reflections.Reflections;
import com.tvd12.reflections.ReflectionsException;
import com.tvd12.reflections.vfs.Vfs.Dir;

public class UrlTypeVFSTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private final UrlTypeVFS type = new UrlTypeVFS();

    @Test
    public void matchesVfsProtocols() throws Exception {
        assertTrue(type.matches(url("vfszip:/deployment/app.jar/")));
        assertTrue(type.matches(url("vfsfile:/deployment/app.jar")));
        assertFalse(type.matches(new URL("file:/deployment/app.jar")));
    }

    @Test
    public void adaptURLReplacesVfsFileProtocol() throws Exception {
        URL adapted = type.adaptURL(url("vfsfile:/deployment/app.jar"));

        assertEquals("file", adapted.getProtocol());
        assertEquals("/deployment/app.jar", adapted.getPath());
    }

    @Test
    public void adaptURLReturnsUrlForOtherProtocols() throws Exception {
        URL url = new URL("file:/deployment/app.jar");

        assertSame(url, type.adaptURL(url));
    }

    @Test
    public void createDirReadsJarFromVfsFileUrl() throws Exception {
        File jar = createJar("test.jar");
        URL url = url("vfsfile:" + jar.toURI().getRawPath());

        Dir dir = type.createDir(url);

        assertNotNull(dir);
        assertEquals(ZipDir.class, dir.getClass());
        assertContains(dir, "sample.txt");
        dir.close();
    }

    @Test
    public void createDirReturnsNullForMissingJar() throws Exception {
        URL url = url("vfsfile:" + new File(folder.getRoot(), "missing.jar").toURI().getRawPath());
        Logger logger = Reflections.log;

        try {
            Reflections.log = null;
            assertNull(type.createDir(url));
        } finally {
            Reflections.log = logger;
        }
    }

    @Test
    public void findFirstMatchOfDeployableExtentionReturnsMatchEnd() {
        String path = "/tmp/demo.ear/lib/app.jar/com/example";

        int first = type.findFirstMatchOfDeployableExtention(path, 0);
        int second = type.findFirstMatchOfDeployableExtention(path, first);

        assertEquals(path.indexOf(".ear/") + ".ear/".length(), first);
        assertEquals(path.indexOf(".jar/") + ".jar/".length(), second);
    }

    @Test
    public void findFirstMatchOfDeployableExtentionReturnsMinusOneWhenMissing() {
        assertEquals(-1, type.findFirstMatchOfDeployableExtention("/tmp/demo.zip", 0));
    }

    @Test(expected = ReflectionsException.class)
    public void replaceZipSeparatorsThrowsWhenNoRealZipFileIsAccepted() throws Exception {
        type.replaceZipSeparators(
            "/tmp/demo.ear/lib/app.jar/com/example",
            file -> false
        );
    }

    private File createJar(String name) throws IOException {
        File jar = folder.newFile(name);
        try (JarOutputStream output = new JarOutputStream(new java.io.FileOutputStream(jar))) {
            output.putNextEntry(new JarEntry("sample.txt"));
            output.write("ok".getBytes("UTF-8"));
            output.closeEntry();
        }
        return jar;
    }

    private void assertContains(Dir dir, String name) {
        for (Vfs.File file : dir.getFiles()) {
            if (name.equals(file.getRelativePath())) {
                return;
            }
        }
        throw new AssertionError("Expected file not found: " + name);
    }

    private static URL url(String spec) throws IOException {
        return new URL(null, spec, new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL url) throws IOException {
                throw new UnsupportedOperationException();
            }
        });
    }
}
