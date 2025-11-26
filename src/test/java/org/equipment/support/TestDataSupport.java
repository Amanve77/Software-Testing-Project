package org.equipment.support;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.equipment.data.DataStore;

/**
 * Reusable helpers for keeping the JSON-backed store isolated during tests.
 */
public final class TestDataSupport {
    private static boolean initialized;

    private TestDataSupport() {
    }

    public static synchronized void initStore() {
        if (initialized) {
            return;
        }
        try {
            Path dir = Paths.get("target", "test-data").toAbsolutePath();
            Files.createDirectories(dir);
            System.setProperty("equipment.data.dir", dir.toString());
            DataStore.seed();
            initialized = true;
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to initialize test data directory", ex);
        }
    }

    public static void resetStore() {
        DataStore.reset();
    }
}



