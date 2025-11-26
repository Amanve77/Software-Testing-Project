package org.equipment.cli;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.equipment.support.TestDataSupport;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MainCLITest {

    @BeforeClass
    public static void setupStore() {
        TestDataSupport.initStore();
    }

    @Before
    public void resetStore() {
        TestDataSupport.resetStore();
    }

    @Test
    public void printDividerOutputsCorrectly() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            MainCLI.printDivider();
            String output = out.toString();
            assertNotNull(output);
            assertTrue(output.contains("------------------------------------------"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void startExitsOnOption3() {
        String input = "3\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            MainCLI.start(scanner);
            String output = out.toString();
            assertTrue(output.contains("Exiting... bye!"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void startHandlesInvalidOption() {
        String input = "99\n3\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            MainCLI.start(scanner);
            String output = out.toString();
            assertTrue(output.contains("Unknown option"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void startShowsMenu() {
        String input = "3\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            MainCLI.start(scanner);
            String output = out.toString();
            assertTrue(output.contains("Equipment Rental Manager"));
            assertTrue(output.contains("Manager Portal"));
            assertTrue(output.contains("Customer Portal"));
        } finally {
            System.setOut(originalOut);
        }
    }
}

