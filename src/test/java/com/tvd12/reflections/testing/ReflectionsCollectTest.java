package com.tvd12.reflections.testing;

import static com.tvd12.reflections.util.Utils.index;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.junit.BeforeClass;
import org.junit.Test;

import com.tvd12.reflections.Reflections;
import com.tvd12.reflections.scanners.MemberUsageScanner;
import com.tvd12.reflections.scanners.MethodAnnotationsScanner;
import com.tvd12.reflections.scanners.MethodParameterNamesScanner;
import com.tvd12.reflections.scanners.MethodParameterScanner;
import com.tvd12.reflections.scanners.ResourcesScanner;
import com.tvd12.reflections.scanners.SubTypesScanner;
import com.tvd12.reflections.scanners.TypeAnnotationsScanner;
import com.tvd12.reflections.serializers.JsonSerializer;
import com.tvd12.reflections.util.ClasspathHelper;
import com.tvd12.reflections.util.ConfigurationBuilder;
import com.tvd12.reflections.util.FilterBuilder;

/** */
public class ReflectionsCollectTest extends ReflectionsTest {

    @BeforeClass
    public static void init() {
        Reflections ref = new Reflections(new ConfigurationBuilder()
                .addUrls(ClasspathHelper.forClass(TestModel.class))
                .filterInputsBy(TestModelFilter)
                .setScanners(
                        new SubTypesScanner(false),
                        new TypeAnnotationsScanner(),
                        new MethodAnnotationsScanner(),
                        new MethodParameterNamesScanner(),
                        new MemberUsageScanner()));

        ref.save(getUserDir() + "/target/test-classes" + "/META-INF/reflections/testModel-reflections.xml");

        ref = new Reflections(new ConfigurationBuilder()
                .setUrls(asList(ClasspathHelper.forClass(TestModel.class)))
                .filterInputsBy(TestModelFilter)
                .setScanners(
                        new MethodParameterScanner()));

        final JsonSerializer serializer = new JsonSerializer();
        ref.save(getUserDir() + "/target/test-classes" + "/META-INF/reflections/testModel-reflections.json", serializer);

        reflections = Reflections
                .collect()
                .merge(Reflections.collect("META-INF/reflections",
                        new FilterBuilder().include(".*-reflections.json"),
                        serializer));
    }

    @Test
    public void testResourcesScanner() {
        Predicate<String> filter = new FilterBuilder().include(".*\\.xml").include(".*\\.json");
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .filterInputsBy(filter)
                .setScanners(new ResourcesScanner())
                .setUrls(asList(ClasspathHelper.forClass(TestModel.class))));

        Set<String> resolved = reflections.getResources(Pattern.compile(".*resource1-reflections\\.xml"));
        assertThat(resolved, are("META-INF/reflections/resource1-reflections.xml"));

        Set<String> resources = reflections.getStore().get(index(ResourcesScanner.class)).keySet();
        assertThat(resources, are("resource1-reflections.xml", "resource2-reflections.xml",
                "testModel-reflections.xml", "testModel-reflections.json"));
    }
}
