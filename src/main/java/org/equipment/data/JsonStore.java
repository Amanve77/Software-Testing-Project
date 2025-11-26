package org.equipment.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Small wrapper around Jackson to simplify reading/writing JSON payloads.
 */
final class JsonStore {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .enable(SerializationFeature.INDENT_OUTPUT);

    private JsonStore() {
    }

    static <T> T read(Path path, TypeReference<T> type, T fallback) {
        if (!Files.exists(path)) {
            return fallback;
        }
        try {
            return MAPPER.readValue(path.toFile(), type);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed reading " + path + ": " + ex.getMessage(), ex);
        }
    }

    static void write(Path path, Object data) {
        try {
            Files.createDirectories(path.getParent());
            MAPPER.writeValue(path.toFile(), data);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed writing " + path + ": " + ex.getMessage(), ex);
        }
    }
}

