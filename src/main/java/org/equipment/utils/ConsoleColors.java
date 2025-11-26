package org.equipment.utils;

/**
 * ANSI color helpers for printing consistent CLI separators.
 */
public final class ConsoleColors {
    public static final String RESET = "\033[0m";
    public static final String CYAN_BOLD_BRIGHT = "\033[1;96m";
    public static final String YELLOW_BOLD = "\033[1;33m";
    public static final String RED_BOLD = "\033[1;31m";

    private ConsoleColors() {
        // no-op
    }
}

