package com.tvd12.reflections.vfs;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import com.tvd12.reflections.Reflections;
import com.tvd12.reflections.util.AbstractIterator;

/** an implementation of {@link com.tvd12.reflections.vfs.Vfs.Dir} for {@link java.util.zip.ZipFile} */
public class ZipDir implements Vfs.Dir {
    final java.util.zip.ZipFile jarFile;

    public ZipDir(JarFile jarFile) {
        this.jarFile = jarFile;
    }

    public String getPath() {
        return jarFile.getName();
    }

    public Iterable<Vfs.File> getFiles() {
        return new Iterable<Vfs.File>() {
            public Iterator<Vfs.File> iterator() {
                return new AbstractIterator<Vfs.File>() {
                    final Enumeration<? extends ZipEntry> entries = jarFile.entries();

                    protected Vfs.File computeNext() {
                        while (entries.hasMoreElements()) {
                            ZipEntry entry = entries.nextElement();
                            if (!entry.isDirectory()) {
                                return new ZipFile(ZipDir.this, entry);
                            }
                        }

                        return endOfData();
                    }
                };
            }
        };
    }

    public void close() {
        try { jarFile.close(); } catch (IOException e) {
            if (Reflections.log != null) {
                Reflections.log.warn("Could not close JarFile", e);
            }
        }
    }

    @Override
    public String toString() {
        return jarFile.getName();
    }
}
