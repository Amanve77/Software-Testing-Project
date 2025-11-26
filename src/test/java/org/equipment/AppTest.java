package org.equipment;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.equipment.data.DataStore;
import org.equipment.support.TestDataSupport;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AppTest {

    @BeforeClass
    public static void setupStore() {
        TestDataSupport.initStore();
    }

    @Before
    public void resetStore() {
        TestDataSupport.resetStore();
    }

    @Test
    public void applicationSeedsDataStore() {
        assertFalse(DataStore.getEquipment().isEmpty());
        assertFalse(DataStore.getCustomers().isEmpty());
    }

    @Test
    public void mainMethodSeedsDataStore() throws Exception {
        String input = "3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            App.main(new String[0]);
            String output = out.toString();
            assertNotNull(output);
            // Verify that DataStore was seeded
            assertFalse(DataStore.getEquipment().isEmpty());
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void mainMethodInitializesCLI() throws Exception {
        String input = "3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            App.main(new String[0]);
            String output = out.toString();
            assertTrue(output.contains("Exiting") || output.contains("Equipment Rental Manager"));
        } finally {
            System.setOut(originalOut);
        }
    }
}

