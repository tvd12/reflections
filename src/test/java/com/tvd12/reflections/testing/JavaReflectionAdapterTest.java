package com.tvd12.reflections.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import org.junit.Test;

import com.tvd12.reflections.adapters.JavaReflectionAdapter;

public class JavaReflectionAdapterTest {

    private final JavaReflectionAdapter adapter = new JavaReflectionAdapter();

    // ---- sample types ----

    @Retention(RetentionPolicy.RUNTIME)
    @interface SampleAnnotation {}

    @SampleAnnotation
    static class SampleClass implements Runnable {
        @SampleAnnotation
        public String field1;
        private int field2;

        public SampleClass() {}
        public SampleClass(String s) {}

        @SampleAnnotation
        public void run() {}

        public String method(@SampleAnnotation String param) { return param; }
        public static int staticMethod() { return 0; }
    }

    static class Child extends SampleClass {}
    interface NoSuper {}

    // ---- getFields ----

    @Test
    public void getFieldsReturnsDeclaredFields() {
        List<Field> fields = adapter.getFields(SampleClass.class);
        // JaCoCo may add a synthetic $jacocoData field at runtime; check >= 2
        long declaredCount = fields.stream().filter(f -> !f.isSynthetic()).count();
        assertEquals(2, declaredCount);
    }

    // ---- getMethods ----

    @Test
    public void getMethodsReturnsDeclaredMethodsAndConstructors() {
        List<Member> members = adapter.getMethods(SampleClass.class);
        long methods = members.stream().filter(m -> m instanceof Method).count();
        long ctors   = members.stream().filter(m -> m instanceof Constructor).count();
        assertTrue(methods >= 3);
        assertEquals(2, ctors);
    }

    // ---- getMethodName ----

    @Test
    public void getMethodNameForMethod() throws Exception {
        Method m = SampleClass.class.getDeclaredMethod("run");
        assertEquals("run", adapter.getMethodName(m));
    }

    @Test
    public void getMethodNameForConstructor() throws Exception {
        Constructor<?> c = SampleClass.class.getDeclaredConstructor();
        assertEquals("<init>", adapter.getMethodName(c));
    }

    @Test
    public void getMethodNameForFieldReturnsNull() throws Exception {
        Field f = SampleClass.class.getDeclaredField("field1");
        assertNull(adapter.getMethodName(f));
    }

    // ---- getParameterNames ----

    @Test
    public void getParameterNamesForNoArgMethod() throws Exception {
        Member m = SampleClass.class.getDeclaredMethod("run");
        assertTrue(adapter.getParameterNames(m).isEmpty());
    }

    @Test
    public void getParameterNamesForMethodWithParam() throws Exception {
        Member m = SampleClass.class.getDeclaredMethod("method", String.class);
        List<String> names = adapter.getParameterNames(m);
        assertEquals(1, names.size());
        assertEquals("java.lang.String", names.get(0));
    }

    @Test
    public void getParameterNamesForConstructorWithParam() throws Exception {
        Member c = SampleClass.class.getDeclaredConstructor(String.class);
        List<String> names = adapter.getParameterNames(c);
        assertEquals(1, names.size());
        assertEquals("java.lang.String", names.get(0));
    }

    @Test
    public void getParameterNamesForFieldReturnsEmpty() throws Exception {
        Field f = SampleClass.class.getDeclaredField("field1");
        assertTrue(adapter.getParameterNames(f).isEmpty());
    }

    // ---- annotation names ----

    @Test
    public void getClassAnnotationNames() {
        List<String> names = adapter.getClassAnnotationNames(SampleClass.class);
        assertTrue(names.contains(SampleAnnotation.class.getName()));
    }

    @Test
    public void getFieldAnnotationNames() throws Exception {
        Field f = SampleClass.class.getDeclaredField("field1");
        List<String> names = adapter.getFieldAnnotationNames(f);
        assertTrue(names.contains(SampleAnnotation.class.getName()));
    }

    @Test
    public void getFieldAnnotationNamesEmpty() throws Exception {
        Field f = SampleClass.class.getDeclaredField("field2");
        assertTrue(adapter.getFieldAnnotationNames(f).isEmpty());
    }

    @Test
    public void getMethodAnnotationNamesForMethod() throws Exception {
        Member m = SampleClass.class.getDeclaredMethod("run");
        List<String> names = adapter.getMethodAnnotationNames(m);
        assertTrue(names.contains(SampleAnnotation.class.getName()));
    }

    @Test
    public void getMethodAnnotationNamesEmptyForUnannotatedMethod() throws Exception {
        Member m = SampleClass.class.getDeclaredMethod("staticMethod");
        List<String> names = adapter.getMethodAnnotationNames(m);
        assertTrue(names.isEmpty());
    }

    @Test
    public void getParameterAnnotationNames() throws Exception {
        Member m = SampleClass.class.getDeclaredMethod("method", String.class);
        List<String> names = adapter.getParameterAnnotationNames(m, 0);
        assertTrue(names.contains(SampleAnnotation.class.getName()));
    }

    @Test
    public void getParameterAnnotationNamesForConstructor() throws Exception {
        Member c = SampleClass.class.getDeclaredConstructor(String.class);
        List<String> names = adapter.getParameterAnnotationNames(c, 0);
        assertNotNull(names);
    }

    // ---- getReturnTypeName ----

    @Test
    public void getReturnTypeName() throws Exception {
        Member m = SampleClass.class.getDeclaredMethod("method", String.class);
        assertEquals("java.lang.String", adapter.getReturnTypeName(m));
    }

    @Test
    public void getReturnTypeNameVoid() throws Exception {
        Member m = SampleClass.class.getDeclaredMethod("run");
        assertEquals("void", adapter.getReturnTypeName(m));
    }

    // ---- getFieldName ----

    @Test
    public void getFieldName() throws Exception {
        Field f = SampleClass.class.getDeclaredField("field1");
        assertEquals("field1", adapter.getFieldName(f));
    }

    // ---- getMethodModifier ----

    @Test
    public void getMethodModifier() throws Exception {
        Member m = SampleClass.class.getDeclaredMethod("run");
        String mod = adapter.getMethodModifier(m);
        assertTrue(mod.contains("public"));
    }

    @Test
    public void getMethodModifierStatic() throws Exception {
        Member m = SampleClass.class.getDeclaredMethod("staticMethod");
        String mod = adapter.getMethodModifier(m);
        assertTrue(mod.contains("static"));
    }

    // ---- getMethodKey / getMethodFullKey ----

    @Test
    public void getMethodKey() throws Exception {
        Member m = SampleClass.class.getDeclaredMethod("method", String.class);
        String key = adapter.getMethodKey(SampleClass.class, m);
        assertEquals("method(java.lang.String)", key);
    }

    @Test
    public void getMethodFullKey() throws Exception {
        Member m = SampleClass.class.getDeclaredMethod("run");
        String key = adapter.getMethodFullKey(SampleClass.class, m);
        assertEquals(SampleClass.class.getName() + ".run()", key);
    }

    // ---- isPublic ----

    @Test
    public void isPublicForPublicClass() {
        assertTrue(adapter.isPublic(String.class));
    }

    @Test
    public void isPublicForPublicMethod() throws Exception {
        Member m = SampleClass.class.getDeclaredMethod("run");
        assertTrue(adapter.isPublic(m));
    }

    @Test
    public void isPublicForPrivateField() throws Exception {
        Member f = SampleClass.class.getDeclaredField("field2");
        assertFalse(adapter.isPublic(f));
    }

    @Test
    public void isPublicForNull() {
        assertFalse(adapter.isPublic(null));
    }

    // ---- getClassName / getSuperclassName / getInterfacesNames ----

    @Test
    public void getClassName() {
        assertEquals(SampleClass.class.getName(), adapter.getClassName(SampleClass.class));
    }

    @Test
    public void getSuperclassName() {
        assertEquals(SampleClass.class.getName(), adapter.getSuperclassName(Child.class));
    }

    @Test
    public void getSuperclassNameForObject() {
        assertEquals("", adapter.getSuperclassName(Object.class));
    }

    @Test
    public void getInterfacesNames() {
        List<String> names = adapter.getInterfacesNames(SampleClass.class);
        assertTrue(names.contains(Runnable.class.getName()));
    }

    @Test
    public void getInterfacesNamesEmpty() {
        List<String> names = adapter.getInterfacesNames(NoSuper.class);
        assertTrue(names.isEmpty());
    }

    // ---- acceptsInput ----

    @Test
    public void acceptsClassFiles() {
        assertTrue(adapter.acceptsInput("com/example/Foo.class"));
    }

    @Test
    public void rejectsNonClassFiles() {
        assertFalse(adapter.acceptsInput("com/example/Foo.java"));
        assertFalse(adapter.acceptsInput("META-INF/MANIFEST.MF"));
    }

    // ---- static getName ----

    @Test
    public void getNameForPrimitive() {
        assertEquals("int", JavaReflectionAdapter.getName(int.class));
    }

    @Test
    public void getNameForArrayType() {
        assertEquals("int[]", JavaReflectionAdapter.getName(int[].class));
        assertEquals("java.lang.String[]", JavaReflectionAdapter.getName(String[].class));
        assertEquals("int[][]", JavaReflectionAdapter.getName(int[][].class));
    }

    @Test
    public void getNameForRegularClass() {
        assertEquals("java.lang.String", JavaReflectionAdapter.getName(String.class));
    }
}
