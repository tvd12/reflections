package com.tvd12.reflections.testing;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.tvd12.reflections.Reflections;
import com.tvd12.reflections.Store;
import com.tvd12.reflections.serializers.XmlSerializer;
import com.tvd12.reflections.util.ConfigurationBuilder;

public class XmlSerializerTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private Reflections buildReflections() {
        return new Reflections(
            new ConfigurationBuilder()
                .forPackages("com.tvd12.reflections.testing")
        );
    }

    @Test
    public void toStringProducesValidXml() {
        Reflections reflections = buildReflections();
        XmlSerializer serializer = new XmlSerializer();
        String xml = serializer.toString(reflections);
        assertNotNull(xml);
        assertTrue(xml.contains("<Reflections>"));
        assertTrue(xml.contains("</Reflections>"));
    }

    @Test
    public void saveWritesXmlFile() throws Exception {
        Reflections reflections = buildReflections();
        XmlSerializer serializer = new XmlSerializer();
        File out = new File(tmp.getRoot(), "reflections.xml");
        File result = serializer.save(reflections, out.getAbsolutePath());
        assertNotNull(result);
        assertTrue(result.exists());
        String content = new String(Files.readAllBytes(result.toPath()));
        assertTrue(content.contains("<Reflections>"));
    }

    @Test
    public void readParsesXmlProducedByToString() {
        Reflections original = buildReflections();
        XmlSerializer serializer = new XmlSerializer();
        String xml = serializer.toString(original);

        // read back the XML
        Reflections parsed = serializer.read(
            new ByteArrayInputStream(xml.getBytes())
        );
        assertNotNull(parsed);
        Store store = parsed.getStore();
        assertNotNull(store);
    }

    @Test
    public void saveAndReadRoundTrip() throws Exception {
        Reflections original = buildReflections();
        XmlSerializer serializer = new XmlSerializer();
        File out = new File(tmp.getRoot(), "rt.xml");
        serializer.save(original, out.getAbsolutePath());

        Reflections parsed = serializer.read(Files.newInputStream(out.toPath()));
        assertNotNull(parsed);
        assertNotNull(parsed.getStore());
    }
}
