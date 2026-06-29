package com.tvd12.reflections.vfs;

import com.tvd12.reflections.util.AbstractIterator;
import com.tvd12.reflections.util.Lists;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/*
 * An implementation of {@link com.tvd12.reflections.vfs.Vfs.Dir} for directory {@link java.io.File}.
 */
public class SystemDir implements Vfs.Dir {
    private final File file;

    public SystemDir(File file) {
        if (file != null && (!file.isDirectory() || !file.canRead())) {
            throw new RuntimeException("cannot use dir " + file);
        }

        this.file = file;
    }

    public String getPath() {
        if (file == null) {
            return "/NO-SUCH-DIRECTORY/";
        }
        return file.getPath().replace("\\", "/");
    }

    public Iterable<Vfs.File> getFiles() {
        if (file == null || !file.exists()) {
            return Collections.emptyList();
        }
        return () -> new AbstractIterator<Vfs.File>() {
            final Deque<File> stack = new ArrayDeque<>(listFiles(file));

            protected Vfs.File computeNext() {
                while (!stack.isEmpty()) {
                    final File file = stack.removeLast();
                    if (file.isDirectory()) {
                        stack.addAll(listFiles(file));
                    } else {
                        return new SystemFile(SystemDir.this, file);
                    }
                }

                return endOfData();
            }
        };
    }

    private static List<File> listFiles(final File file) {
        File[] files = file.listFiles();
        return files != null
            ? Lists.newArrayList(files)
            : Lists.newArrayList();
    }

    public void close() {}

    @Override
    public String toString() {
        return getPath();
    }
}
