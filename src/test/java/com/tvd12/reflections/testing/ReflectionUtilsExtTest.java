package com.tvd12.reflections.testing;

import static com.tvd12.reflections.ReflectionUtils.getAllAnnotations;
import static com.tvd12.reflections.ReflectionUtils.getAllMethodList;
import static com.tvd12.reflections.ReflectionUtils.getMethodList;
import static com.tvd12.reflections.ReflectionUtils.withClassModifier;
import static com.tvd12.reflections.ReflectionUtils.withPrefix;
import static com.tvd12.reflections.ReflectionUtils.withType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewMethod;
import org.junit.Test;

import com.tvd12.reflections.ReflectionUtils;

@SuppressWarnings("unchecked")
public class ReflectionUtilsExtTest {

    // ---- getAllMethodList / getMethodList ----

    @Test
    public void getAllMethodListReturnsAllInheritedMethods() {
        List<Method> methods = getAllMethodList(TestModel.C4.class);
        assertFalse(methods.isEmpty());
    }

    @Test
    public void getAllMethodListWithPrefixFilter() {
        List<Method> getters = getAllMethodList(TestModel.C4.class, withPrefix("get"));
        assertTrue(getters.isEmpty()); // C4 has no get* methods
    }

    @Test
    public void getMethodListForInterface() {
        List<Method> methods = getMethodList(Runnable.class);
        assertEquals(1, methods.size());
        assertEquals("run", methods.get(0).getName());
    }

    @Test
    public void getMethodListWithPrefixFilter() {
        List<Method> methods = getMethodList(TestModel.C4.class, withPrefix("m"));
        assertFalse(methods.isEmpty());
        for (Method m : methods) {
            assertTrue(m.getName().startsWith("m"));
        }
    }

    // ---- getAllAnnotations ----

    @Test
    public void getAllAnnotationsOnClassHierarchy() {
        Set<Annotation> annotations = getAllAnnotations(TestModel.C3.class);
        assertFalse(annotations.isEmpty());
    }

    @Test
    public void getAllAnnotationsOnNonClass() throws Exception {
        Method m = TestModel.C4.class.getDeclaredMethod("m1");
        Set<Annotation> annotations = getAllAnnotations(m);
        assertNotNull(annotations);
    }

    // ---- withPrefix ----

    @Test
    public void withPrefixMatchesMethods() {
        Set<Method> methods = ReflectionUtils.getMethods(
            TestModel.C4.class,
            withPrefix("m")
        );
        for (Method m : methods) {
            assertTrue(m.getName().startsWith("m"));
        }
    }

    @Test
    public void withPrefixNullInputReturnsFalse() {
        assertFalse(withPrefix("get").test(null));
    }

    // ---- withType (Field predicate) ----

    @Test
    public void withTypeMatchesFieldByExactType() {
        Set<Field> fields = ReflectionUtils.getFields(
            TestModel.C4.class,
            withType(String.class)
        );
        assertFalse(fields.isEmpty());
        for (Field f : fields) {
            if (!f.isSynthetic()) {
                assertEquals(String.class, f.getType());
            }
        }
    }

    @Test
    public void withTypeNullInputReturnsFalse() {
        assertFalse(withType(String.class).test(null));
    }

    // ---- withClassModifier ----

    @Test
    public void withClassModifierMatchesPublicClass() {
        assertTrue(withClassModifier(Modifier.PUBLIC).test(TestModel.C4.class));
    }

    @Test
    public void withClassModifierRejectsFinalWhenNotFinal() {
        assertFalse(withClassModifier(Modifier.FINAL).test(TestModel.C4.class));
    }

    @Test
    public void withClassModifierNullInputReturnsFalse() {
        assertFalse(withClassModifier(Modifier.PUBLIC).test(null));
    }

    // ---- forName ----

    @Test
    public void forNameResolvesKnownClass() {
        Class<?> clazz = ReflectionUtils.forName("java.lang.String");
        assertEquals(String.class, clazz);
    }

    @Test
    public void forNameResolvesPrimitiveInt() {
        Class<?> clazz = ReflectionUtils.forName("int");
        assertEquals(int.class, clazz);
    }

    @Test
    public void forNameResolvesIntArray() {
        Class<?> clazz = ReflectionUtils.forName("int[]");
        assertEquals(int[].class, clazz);
    }

    @Test
    public void forNameResolvesObjectArray() {
        Class<?> clazz = ReflectionUtils.forName("java.lang.String[]");
        assertNotNull(clazz);
        assertTrue(clazz.isArray());
        assertEquals(String.class, clazz.getComponentType());
    }

    @Test
    public void forNameReturnsNullForUnknownClass() {
        Class<?> clazz = ReflectionUtils.forName("com.nonexistent.ClassName12345");
        assertNull(clazz);
    }

    // ---- toClass ----

    @Test
    public void toClassConvertsCtClass() throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.makeClass(
            "com.tvd12.reflections.ReflectionUtilsToClassGenerated"
                + System.nanoTime()
        );
        ctClass.addMethod(CtNewMethod.make(
            "public String message() { return \"ok\"; }",
            ctClass
        ));

        Class<?> clazz = ReflectionUtils.toClass(
            ctClass,
            ReflectionUtils.class
        );
        Object instance = clazz.newInstance();

        assertEquals(
            "ok",
            clazz.getMethod("message").invoke(instance)
        );
    }

    @Test(expected = ClassCastException.class)
    public void toClassThrowsWhenImplClassIsNotCtClass() throws Exception {
        ReflectionUtils.toClass(
            "not a CtClass",
            ReflectionUtils.class
        );
    }

    // ---- withAnnotations (by class array) ----

    @Test
    public void withAnnotationsByClassMatchesElement() throws Exception {
        // C4.f1 has @AF1("1"), so withAnnotations(AF1.class) should match
        Field f1 = TestModel.C4.class.getDeclaredField("f1");
        assertTrue(
            ReflectionUtils.withAnnotations(TestModel.AF1.class).test(f1)
        );
    }

    @Test
    public void withAnnotationsByClassRejectsNonMatch() throws Exception {
        Field f3 = TestModel.C4.class.getDeclaredField("f3");
        assertFalse(
            ReflectionUtils.withAnnotations(TestModel.AF1.class).test(f3)
        );
    }

    // ---- withAnnotations (by annotation instance array) ----

    @Test
    public void withAnnotationsByInstanceMatchesExactAnnotation() throws Exception {
        // C4.f1 has @AF1("1"); create an instance of AF1 with value "1" to match
        Field f1 = TestModel.C4.class.getDeclaredField("f1");
        // get the actual annotation from the field
        TestModel.AF1 af1 = f1.getAnnotation(TestModel.AF1.class);
        assertTrue(
            ReflectionUtils.withAnnotations(af1).test(f1)
        );
    }

    @Test
    public void withAnnotationsByInstanceNullRejectsFalse() throws Exception {
        assertFalse(ReflectionUtils.withAnnotations(new Annotation[0]).test(null));
    }

    @Test
    public void withAnnotationsByInstanceLengthMismatchReturnsFalse() throws Exception {
        Field f1 = TestModel.C4.class.getDeclaredField("f1");
        TestModel.AF1 af1 = f1.getAnnotation(TestModel.AF1.class);
        // f3 has no annotations, so annotation count mismatch
        Field f3 = TestModel.C4.class.getDeclaredField("f3");
        assertFalse(ReflectionUtils.withAnnotations(af1).test(f3));
    }
}
