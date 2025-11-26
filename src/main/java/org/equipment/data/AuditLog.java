package org.equipment.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Append-only audit log stored under the data directory.
 */
public final class AuditLog {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
    private static Path logPath;

    private AuditLog() {
    }

    static void configure(Path dataDir) {
        logPath = dataDir.resolve("audit.log");
    }

    public static void write(String actor, String action) {
        if (logPath == null) {
            return;
        }
        String line = String.format("%s | %s | %s%n", LocalDateTime.now().format(FORMATTER), actor, action);
        try {
            Files.createDirectories(logPath.getParent());
            Files.write(logPath, line.getBytes(StandardCharsets.UTF_8),
                    Files.exists(logPath) ? java.nio.file.StandardOpenOption.APPEND
                            : java.nio.file.StandardOpenOption.CREATE);
        } catch (IOException ex) {
            // Logging failure should not break core logic; swallow after printing.
            System.err.println("Failed to write audit log: " + ex.getMessage());
        }
    }

    public static String readAll() {
        if (logPath == null || !Files.exists(logPath)) {
            return "No audit entries yet.";
        }
        try {
            return new String(Files.readAllBytes(logPath), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            return "Unable to read audit log: " + ex.getMessage();
        }
    }
}

