package org.equipment.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.equipment.support.TestDataSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AuditLogTest {

    private static Path testLogPath;

    @BeforeClass
    public static void setupStore() {
        TestDataSupport.initStore();
    }

    @Before
    public void setup() throws IOException {
        Path testDir = Paths.get("target", "test-audit").toAbsolutePath();
        Files.createDirectories(testDir);
        testLogPath = testDir.resolve("audit.log");
        AuditLog.configure(testDir);
    }

    @After
    public void cleanup() throws IOException {
        if (testLogPath != null && Files.exists(testLogPath)) {
            Files.deleteIfExists(testLogPath);
        }
        if (testLogPath != null && Files.exists(testLogPath.getParent())) {
            Files.deleteIfExists(testLogPath.getParent());
        }
    }

    @Test
    public void writeCreatesLogEntry() {
        AuditLog.write("test:actor", "test action");
        String content = AuditLog.readAll();
        assertNotNull(content);
        assertFalse(content.isEmpty());
        assertTrue(content.contains("test:actor"));
        assertTrue(content.contains("test action"));
    }

    @Test
    public void writeAppendsMultipleEntries() {
        AuditLog.write("actor1", "action1");
        AuditLog.write("actor2", "action2");
        String content = AuditLog.readAll();
        assertTrue(content.contains("action1"));
        assertTrue(content.contains("action2"));
    }

    @Test
    public void readAllReturnsMessageWhenLogDoesNotExist() throws IOException {
        Path nonExistentDir = Paths.get("target", "non-existent-audit-" + System.currentTimeMillis()).toAbsolutePath();
        // Ensure the directory and file don't exist
        Path logFile = nonExistentDir.resolve("audit.log");
        if (Files.exists(logFile)) {
            Files.delete(logFile);
        }
        if (Files.exists(nonExistentDir)) {
            Files.deleteIfExists(nonExistentDir);
        }
        AuditLog.configure(nonExistentDir);
        String content = AuditLog.readAll();
        assertEquals("No audit entries yet.", content);
    }

    @Test
    public void writeHandlesInvalidPath() {
        // Test that write doesn't throw exception even with problematic path
        Path invalidPath = Paths.get("target", "invalid", "path", "that", "might", "fail");
        AuditLog.configure(invalidPath);
        // Should not throw exception even if path is problematic
        AuditLog.write("test", "action");
    }

    @Test
    public void readAllHandlesNonExistentPath() {
        // Test with a path that doesn't exist
        Path nonExistentPath = Paths.get("target", "non-existent-audit-read").toAbsolutePath();
        AuditLog.configure(nonExistentPath);
        String content = AuditLog.readAll();
        assertEquals("No audit entries yet.", content);
    }

    @Test
    public void configureSetsLogPath() {
        Path testDir = Paths.get("target", "test-configure").toAbsolutePath();
        AuditLog.configure(testDir);
        AuditLog.write("test", "configure test");
        String content = AuditLog.readAll();
        assertTrue(content.contains("configure test"));
    }
}

