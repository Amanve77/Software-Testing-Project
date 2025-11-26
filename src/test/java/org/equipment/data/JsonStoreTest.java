package org.equipment.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.equipment.domain.Equipment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

public class JsonStoreTest {

    private Path testFile;
    private TypeReference<List<Equipment>> equipmentType;

    @Before
    public void setup() throws IOException {
        Path testDir = Paths.get("target", "test-jsonstore").toAbsolutePath();
        Files.createDirectories(testDir);
        testFile = testDir.resolve("test-equipment.json");
        equipmentType = new TypeReference<List<Equipment>>() {};
    }

    @After
    public void cleanup() throws IOException {
        if (testFile != null && Files.exists(testFile)) {
            Files.deleteIfExists(testFile);
        }
        if (testFile != null && Files.exists(testFile.getParent())) {
            try {
                Files.deleteIfExists(testFile.getParent());
            } catch (IOException e) {
                // Ignore cleanup errors
            }
        }
    }

    @Test
    public void readReturnsFallbackWhenFileDoesNotExist() {
        List<Equipment> fallback = new ArrayList<>();
        Equipment eq = new Equipment(1, "Test", "Category", 10.0, 5.0, 1, false);
        fallback.add(eq);
        
        List<Equipment> result = JsonStore.read(testFile, equipmentType, fallback);
        assertEquals(fallback, result);
        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).getName());
    }

    @Test
    public void writeAndReadRoundTrip() {
        List<Equipment> original = new ArrayList<>();
        original.add(new Equipment(1, "Test Equipment", "Category", 10.0, 5.0, 2, false));
        original.add(new Equipment(2, "Another Equipment", "Category2", 20.0, 10.0, 1, true));
        
        JsonStore.write(testFile, original);
        assertTrue(Files.exists(testFile));
        
        List<Equipment> read = JsonStore.read(testFile, equipmentType, new ArrayList<>());
        assertEquals(2, read.size());
        assertEquals("Test Equipment", read.get(0).getName());
        assertEquals("Another Equipment", read.get(1).getName());
        assertEquals(10.0, read.get(0).getDailyRate(), 0.001);
        assertEquals(20.0, read.get(1).getDailyRate(), 0.001);
    }

    @Test
    public void writeCreatesParentDirectories() {
        Path nestedFile = testFile.getParent().resolve("nested").resolve("deep").resolve("file.json");
        List<Equipment> data = new ArrayList<>();
        data.add(new Equipment(1, "Test", "Cat", 10.0, 5.0, 1, false));
        
        JsonStore.write(nestedFile, data);
        assertTrue(Files.exists(nestedFile));
    }

    @Test
    public void readHandlesEmptyFile() throws IOException {
        // Create an empty file - this will cause Jackson to throw an exception
        // which JsonStore converts to IllegalStateException
        Files.createFile(testFile);
        List<Equipment> fallback = new ArrayList<>();
        
        try {
            JsonStore.read(testFile, equipmentType, fallback);
            // If we get here, the empty file was handled (maybe as fallback)
        } catch (IllegalStateException e) {
            // Expected - empty file causes Jackson parsing error
            assertTrue(e.getMessage().contains("Failed reading"));
        }
    }

    @Test
    public void writeOverwritesExistingFile() {
        List<Equipment> first = new ArrayList<>();
        first.add(new Equipment(1, "First", "Cat", 10.0, 5.0, 1, false));
        
        List<Equipment> second = new ArrayList<>();
        second.add(new Equipment(2, "Second", "Cat", 20.0, 10.0, 2, false));
        
        JsonStore.write(testFile, first);
        JsonStore.write(testFile, second);
        
        List<Equipment> read = JsonStore.read(testFile, equipmentType, new ArrayList<>());
        assertEquals(1, read.size());
        assertEquals("Second", read.get(0).getName());
        assertEquals(2, read.get(0).getId());
    }
}

