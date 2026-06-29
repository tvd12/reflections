package com.tvd12.reflections.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.tvd12.reflections.util.ClasspathHelper;
import com.tvd12.reflections.vfs.Vfs;
import com.tvd12.reflections.vfs.Vfs.DefaultUrlTypes;

public class VfsExtTest {

    // ---- getDefaultUrlTypes / setDefaultURLTypes / addDefaultURLTypes ----

    @Test
    public void getDefaultUrlTypesReturnsNonEmpty() {
        assertFalse(Vfs.getDefaultUrlTypes().isEmpty());
    }

    @Test
    public void setDefaultURLTypesReplacesAll() {
        List<Vfs.UrlType> original = new ArrayList<>(Vfs.getDefaultUrlTypes());
        try {
            Vfs.setDefaultURLTypes(Collections.emptyList());
            assertTrue(Vfs.getDefaultUrlTypes().isEmpty());
        } finally {
            Vfs.setDefaultURLTypes(original);
        }
    }

    @Test
    public void addDefaultURLTypesAddsToList() {
        List<Vfs.UrlType> original = new ArrayList<>(Vfs.getDefaultUrlTypes());
        int before = original.size();
        Vfs.UrlType dummy = new Vfs.UrlType() {
            public boolean matches(URL url) { return false; }
            public Vfs.Dir createDir(URL url) { return null; }
        };
        try {
            Vfs.addDefaultURLTypes(dummy);
            assertEquals(before + 1, Vfs.getDefaultUrlTypes().size());
        } finally {
            Vfs.setDefaultURLTypes(original);
        }
    }

    // ---- findFiles with Predicate ----

    @Test
    public void findFilesWithPredicateFindsClassFiles() {
        Collection<URL> urls = ClasspathHelper.forPackage("com.tvd12.reflections.testing");
        Iterable<Vfs.File> files = Vfs.findFiles(urls, f -> f.getName().endsWith(".class"));
        List<String> names = new ArrayList<>();
        for (Vfs.File f : files) {
            names.add(f.getName());
        }
        assertFalse(names.isEmpty());
        assertTrue(names.stream().allMatch(n -> n.endsWith(".class")));
    }

    @Test
    public void findFilesWithStringPredicateAndPackage() {
        Collection<URL> urls = ClasspathHelper.forPackage("com.tvd12.reflections.testing");
        Iterable<Vfs.File> files = Vfs.findFiles(
            urls,
            "com/tvd12/reflections/testing",
            f -> f.endsWith(".class")
        );
        assertNotNull(files);
        assertTrue(files.iterator().hasNext());
    }

    // ---- Vfs$1.getRelativePath (via findFiles/toMemoryFile) ----

    @Test
    public void findFilesMemoryFileGetRelativePath() {
        Collection<URL> urls = ClasspathHelper.forPackage("com.tvd12.reflections.testing");
        Iterable<Vfs.File> files = Vfs.findFiles(urls, f -> f.getName().endsWith(".class"));
        boolean found = false;
        for (Vfs.File f : files) {
            String rel = f.getRelativePath();
            assertNotNull(rel);
            assertTrue(rel.endsWith(".class"));
            found = true;
            break;
        }
        assertTrue("Expected at least one .class file", found);
    }

    // ---- Vfs.fromURL(URL, UrlType...) varargs ----

    @Test
    public void fromURLWithVarargsUrlTypes() throws Exception {
        URL url = ClasspathHelper.forClass(VfsExtTest.class);
        Vfs.Dir dir = Vfs.fromURL(url, DefaultUrlTypes.directory, DefaultUrlTypes.jarFile);
        assertNotNull(dir);
        dir.close();
    }
}
