package com.tvd12.reflections.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.tvd12.reflections.adapters.JavaReflectionAdapter;
import com.tvd12.reflections.vfs.Vfs;

public class JavaReflectionAdapterExtTest {

    private final JavaReflectionAdapter adapter = new JavaReflectionAdapter();

    // Minimal Vfs.File that maps to a known class on the classpath
    private static Vfs.File makeClassFile(final String relativePath) {
        return new Vfs.File() {
            public String getName() {
                int slash = relativePath.lastIndexOf('/');
                return slash >= 0 ? relativePath.substring(slash + 1) : relativePath;
            }
            public String getRelativePath() { return relativePath; }
            public InputStream openInputStream() throws IOException { return null; }
        };
    }

    @Test
    public void getOrCreateClassObjectNoLoaderResolvesKnownClass() {
        // com/tvd12/reflections/testing/TestModel.class → TestModel
        String relPath = TestModel.class.getName().replace('.', '/') + ".class";
        Vfs.File file = makeClassFile(relPath);
        Class<?> cls = adapter.getOrCreateClassObject(file);
        assertNotNull(cls);
        assertEquals(TestModel.class, cls);
    }

    @Test
    public void getOrCreateClassObjectWithLoaderResolvesKnownClass() {
        String relPath = TestModel.class.getName().replace('.', '/') + ".class";
        Vfs.File file = makeClassFile(relPath);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Class<?> cls = adapter.getOrCreateClassObject(file, cl);
        assertNotNull(cls);
        assertEquals(TestModel.class, cls);
    }

    @Test
    public void getOrCreateClassObjectReturnsNullForUnknown() {
        Vfs.File file = makeClassFile("com/nonexistent/Ghost.class");
        Class<?> cls = adapter.getOrCreateClassObject(file);
        assertNull(cls);
    }
}
