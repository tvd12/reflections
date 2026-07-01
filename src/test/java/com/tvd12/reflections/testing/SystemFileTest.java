package com.tvd12.reflections.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tvd12.reflections.vfs.SystemDir;
import com.tvd12.reflections.vfs.SystemFile;

public class SystemFileTest {

    private Path tempDir;
    private Path tempFile;

    @Before
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory("system-file-test");
        tempFile = Files.createTempFile(tempDir, "test", ".txt");
        Files.write(tempFile, "hello".getBytes());
    }

    @After
    public void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
        Files.deleteIfExists(tempDir);
    }

    private SystemFile makeSystemFile() {
        SystemDir root = new SystemDir(tempDir.toFile());
        return new SystemFile(root, tempFile.toFile());
    }

    @Test
    public void getNameReturnsFilename() {
        SystemFile sf = makeSystemFile();
        assertEquals(tempFile.getFileName().toString(), sf.getName());
    }

    @Test
    public void getRelativePathReturnsPathRelativeToRoot() {
        SystemFile sf = makeSystemFile();
        String rel = sf.getRelativePath();
        assertNotNull(rel);
        assertTrue(rel.endsWith(".txt"));
        assertTrue(!rel.startsWith("/"));
    }

    @Test
    public void openInputStreamReadsContent() throws IOException {
        SystemFile sf = makeSystemFile();
        try (InputStream is = sf.openInputStream()) {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            byte[] chunk = new byte[64];
            int n;
            while ((n = is.read(chunk)) != -1) {
                buf.write(chunk, 0, n);
            }
            assertEquals("hello", buf.toString());
        }
    }

    @Test
    public void toStringReturnsFilePath() {
        SystemFile sf = makeSystemFile();
        String str = sf.toString();
        assertNotNull(str);
        assertTrue(str.contains("test"));
    }

    @Test
    public void systemDirGetFilesIteratesSystemFiles() {
        SystemDir root = new SystemDir(tempDir.toFile());
        int count = 0;
        for (com.tvd12.reflections.vfs.Vfs.File f : root.getFiles()) {
            assertNotNull(f.getName());
            assertNotNull(f.getRelativePath());
            count++;
        }
        assertEquals(1, count);
    }
}
