package org.equipment.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ConsoleColorsTest {

    @Test
    public void resetConstantIsDefined() {
        assertNotNull(ConsoleColors.RESET);
        assertTrue(ConsoleColors.RESET.length() > 0);
    }

    @Test
    public void cyanBoldBrightConstantIsDefined() {
        assertNotNull(ConsoleColors.CYAN_BOLD_BRIGHT);
        assertTrue(ConsoleColors.CYAN_BOLD_BRIGHT.length() > 0);
    }

    @Test
    public void yellowBoldConstantIsDefined() {
        assertNotNull(ConsoleColors.YELLOW_BOLD);
        assertTrue(ConsoleColors.YELLOW_BOLD.length() > 0);
    }

    @Test
    public void redBoldConstantIsDefined() {
        assertNotNull(ConsoleColors.RED_BOLD);
        assertTrue(ConsoleColors.RED_BOLD.length() > 0);
    }

    @Test
    public void constantsContainAnsiCodes() {
        assertTrue(ConsoleColors.RESET.contains("\033"));
        assertTrue(ConsoleColors.CYAN_BOLD_BRIGHT.contains("\033"));
        assertTrue(ConsoleColors.YELLOW_BOLD.contains("\033"));
        assertTrue(ConsoleColors.RED_BOLD.contains("\033"));
    }
}

