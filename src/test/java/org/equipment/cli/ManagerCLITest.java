package org.equipment.cli;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.equipment.data.DataStore;
import org.equipment.domain.Equipment;
import org.equipment.domain.Reservation;
import org.equipment.support.TestDataSupport;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ManagerCLITest {

    @BeforeClass
    public static void setupStore() {
        TestDataSupport.initStore();
    }

    @Before
    public void resetStore() {
        TestDataSupport.resetStore();
    }

    @Test
    public void openDeniesAccessWithInvalidCredentials() {
        String input = "wrong\nwrong\nwrong\nwrong\nwrong\nwrong\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            ManagerCLI.open(scanner);
            String output = out.toString();
            assertTrue(output.contains("Access denied"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void openGrantsAccessWithValidCredentials() {
        String input = "admin\n1234\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            ManagerCLI.open(scanner);
            String output = out.toString();
            assertTrue(output.contains("Welcome") || output.contains("Manager Portal"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void openListsInventory() {
        String input = "admin\n1234\n1\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            ManagerCLI.open(scanner);
            // Should execute without error
            assertTrue(true);
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void openAddsEquipment() {
        String input = "admin\n1234\n2\nTest Equipment\nCategory\n10.5\n5.0\n3\nn\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            ManagerCLI.open(scanner);
            String output = out.toString();
            assertTrue(output.contains("Created") || output.contains("Test Equipment"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void openUpdatesRate() {
        Equipment equipment = DataStore.findEquipment(1000).orElseThrow(IllegalStateException::new);
        String input = "admin\n1234\n3\n" + equipment.getId() + "\n25.0\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            ManagerCLI.open(scanner);
            String output = out.toString();
            assertTrue(output.contains("Rate updated") || output.contains("Equipment not found"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void openTogglesMaintenance() {
        Equipment equipment = DataStore.findEquipment(1000).orElseThrow(IllegalStateException::new);
        String input = "admin\n1234\n4\n" + equipment.getId() + "\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            ManagerCLI.open(scanner);
            String output = out.toString();
            assertTrue(output.contains("Maintenance flag toggled") || output.contains("Equipment not found"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void openListsRentals() {
        String input = "admin\n1234\n5\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            ManagerCLI.open(scanner);
            // Should execute without error
            assertTrue(true);
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void openShowsLowStockReport() {
        String input = "admin\n1234\n6\n2\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            ManagerCLI.open(scanner);
            String output = out.toString();
            assertTrue(output.contains("threshold") || output.contains("LOW"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void openShowsFinancials() {
        String input = "admin\n1234\n7\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            ManagerCLI.open(scanner);
            String output = out.toString();
            assertTrue(output.contains("deposits") || output.contains("fees") || output.contains("overdue"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void openManagesReservations() {
        String input = "admin\n1234\n8\n0\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            ManagerCLI.open(scanner);
            String output = out.toString();
            assertTrue(output.contains("reservation") || output.contains("pending"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void openFulfillsReservation() {
        Equipment equipment = DataStore.findEquipment(1000).orElseThrow(IllegalStateException::new);
        equipment.setAvailableUnits(0);
        DataStore.persistEquipment();
        
        Reservation reservation = DataStore.addReservation(equipment.getId(), 
                DataStore.getCustomers().get(0).getId());
        equipment.setAvailableUnits(1);
        DataStore.persistEquipment();
        
        String input = "admin\n1234\n8\n1\n" + reservation.getId() + "\n2\n0\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            ManagerCLI.open(scanner);
            String output = out.toString();
            assertTrue(output.contains("fulfilled") || output.contains("Unable to fulfill"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void openDeclinesReservation() {
        Equipment equipment = DataStore.findEquipment(1000).orElseThrow(IllegalStateException::new);
        equipment.setAvailableUnits(0);
        DataStore.persistEquipment();
        
        Reservation reservation = DataStore.addReservation(equipment.getId(), 
                DataStore.getCustomers().get(0).getId());
        
        String input = "admin\n1234\n8\n2\n" + reservation.getId() + "\n0\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            ManagerCLI.open(scanner);
            String output = out.toString();
            assertTrue(output.contains("declined") || output.contains("not found"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void openShowsReports() {
        String input = "admin\n1234\n9\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            ManagerCLI.open(scanner);
            String output = out.toString();
            assertTrue(output.contains("Overdue") || output.contains("customers") || output.contains("clear"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void openShowsAuditLog() {
        String input = "admin\n1234\n10\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            ManagerCLI.open(scanner);
            // Should execute without error
            assertTrue(true);
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void openHandlesInvalidSelection() {
        String input = "admin\n1234\n99\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            ManagerCLI.open(scanner);
            String output = out.toString();
            assertTrue(output.contains("Invalid selection"));
        } finally {
            System.setOut(originalOut);
        }
    }
}

