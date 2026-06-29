package com.tvd12.reflections.serializers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tvd12.reflections.Reflections;
import com.tvd12.reflections.io.Files;
import com.tvd12.reflections.util.Multimap;
import com.tvd12.reflections.util.Multimaps;
import com.tvd12.reflections.util.SetMultimap;
import com.tvd12.reflections.util.Sets;
import com.tvd12.reflections.util.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/** serialization of Reflections to json
 *
 * <p>an example of produced json:
 * <pre>
 * {"store":{"storeMap":
 *    {"com.tvd12.reflections.scanners.TypeAnnotationsScanner":{
 *       "com.tvd12.reflections.TestModel$AC1":["com.tvd12.reflections.TestModel$C1"],
 *       "com.tvd12.reflections.TestModel$AC2":["com.tvd12.reflections.TestModel$I3",
 * ...
 * </pre>
 * */
@SuppressWarnings("rawtypes")
public class JsonSerializer implements Serializer {
    private Gson gson;

    public Reflections read(InputStream inputStream) {
        return getGson().fromJson(
            new InputStreamReader(inputStream),
            Reflections.class
        );
    }

    public File save(Reflections reflections, String filename) {
        try {
            File file = Utils.prepareFile(filename);
            Files.write(
                toString(reflections),
                file,
                Charset.defaultCharset()
            );
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String toString(Reflections reflections) {
        return getGson().toJson(reflections);
    }

    private Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                .registerTypeAdapter(
                    Multimap.class,
                    (com.google.gson.JsonSerializer<Multimap>) (
                        multimap,
                        type,
                        jsonSerializationContext) ->
                        jsonSerializationContext.serialize(multimap.asMap())
                )
                .registerTypeAdapter(Multimap.class,
                    (JsonDeserializer<Multimap>) (
                        jsonElement,
                        type,
                        jsonDeserializationContext
                    ) -> {
                        final SetMultimap<String,String> map = Multimaps
                            .newSetMultimap(new HashMap<>(), Sets::newHashSet);
                        for (Map.Entry<String, JsonElement> entry
                            : ((JsonObject) jsonElement).entrySet()
                        ) {
                            for (JsonElement element : (JsonArray) entry.getValue()) {
                                map.put(entry.getKey(), element.getAsString());
                            }
                        }
                        return map;
                    }
                )
                .setPrettyPrinting()
                .create();
        }
        return gson;
    }
}
