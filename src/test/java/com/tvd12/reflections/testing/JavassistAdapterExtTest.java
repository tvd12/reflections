package com.tvd12.reflections.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;

import org.junit.Test;

import com.tvd12.reflections.adapters.JavassistAdapter;

import javassist.bytecode.ClassFile;
import javassist.bytecode.MethodInfo;

public class JavassistAdapterExtTest {

    private final JavassistAdapter adapter = new JavassistAdapter();

    private ClassFile loadClassFile(Class<?> cls) throws Exception {
        String path = cls.getName().replace('.', '/') + ".class";
        try (InputStream is = cls.getClassLoader().getResourceAsStream(path)) {
            return new ClassFile(new DataInputStream(new BufferedInputStream(is)));
        }
    }

    @Test
    public void isPublicForPublicClass() throws Exception {
        ClassFile cf = loadClassFile(MethodHolder.class);
        assertTrue(adapter.isPublic(cf));
    }

    @Test
    public void isPublicForPublicMethod() throws Exception {
        ClassFile cf = loadClassFile(MethodHolder.class);
        MethodInfo mi = null;
        for (Object m : cf.getMethods()) {
            MethodInfo method = (MethodInfo) m;
            if ("publicMethod".equals(method.getName())) {
                mi = method;
                break;
            }
        }
        assertTrue("publicMethod must be found", mi != null);
        assertTrue(adapter.isPublic(mi));
    }

    @Test
    public void isPublicForNonClassFileFieldInfoOrMethodInfo() {
        // unknown type → fallback 0 → AccessFlag.isPublic(0) == false
        assertFalse(adapter.isPublic("notAByteCodeObject"));
    }

    @Test
    public void getMethodModifierForPublicMethod() throws Exception {
        ClassFile cf = loadClassFile(MethodHolder.class);
        MethodInfo pm = null;
        for (Object m : cf.getMethods()) {
            MethodInfo method = (MethodInfo) m;
            if ("publicMethod".equals(method.getName())) {
                pm = method;
                break;
            }
        }
        assertTrue("publicMethod must be found", pm != null);
        assertEquals("public", adapter.getMethodModifier(pm));
    }

    @Test
    public void getMethodModifierForPrivateMethod() throws Exception {
        ClassFile cf = loadClassFile(MethodHolder.class);
        MethodInfo pm = null;
        for (Object m : cf.getMethods()) {
            MethodInfo method = (MethodInfo) m;
            if ("secret".equals(method.getName())) {
                pm = method;
                break;
            }
        }
        assertTrue("secret() must be found", pm != null);
        assertEquals("private", adapter.getMethodModifier(pm));
    }

    // Helper class — in the test package so its .class is on the test classpath
    public static class MethodHolder {
        public void publicMethod() {}
        @SuppressWarnings("unused")
        private void secret() {}
    }
}
