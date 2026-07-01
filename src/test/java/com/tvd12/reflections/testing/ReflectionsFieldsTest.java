package com.tvd12.reflections.testing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.tvd12.reflections.Reflections;
import com.tvd12.reflections.scanners.FieldAnnotationsScanner;
import com.tvd12.reflections.util.ConfigurationBuilder;

public class ReflectionsFieldsTest {

    private static Reflections reflections;

    @BeforeClass
    public static void setup() {
        reflections = new Reflections(
            new ConfigurationBuilder()
                .forPackages("com.tvd12.reflections.testing")
                .setScanners(new FieldAnnotationsScanner())
        );
    }

    @Test
    public void getFieldsAnnotatedWithByClass() {
        Set<Field> fields = reflections.getFieldsAnnotatedWith(TestModel.AF1.class);
        assertNotNull(fields);
        assertFalse(fields.isEmpty());
        for (Field f : fields) {
            assertNotNull(f.getAnnotation(TestModel.AF1.class));
        }
    }

    @Test
    public void getFieldsAnnotatedWithByInstance() {
        TestModel.C4 c4 = new TestModel.C4("x");
        Field f1;
        try {
            f1 = TestModel.C4.class.getDeclaredField("f1");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        TestModel.AF1 af1 = f1.getAnnotation(TestModel.AF1.class);
        Set<Field> fields = reflections.getFieldsAnnotatedWith(af1);
        assertNotNull(fields);
        assertFalse(fields.isEmpty());
    }
}
