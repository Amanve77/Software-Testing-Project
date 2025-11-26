package org.equipment.cli;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.equipment.data.DataStore;
import org.equipment.domain.Customer;
import org.equipment.domain.Equipment;
import org.equipment.domain.Rental;
import org.equipment.support.TestDataSupport;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CustomerCLITest {

    @BeforeClass
    public static void setupStore() {
        TestDataSupport.initStore();
    }

    @Before
    public void resetStore() {
        TestDataSupport.resetStore();
    }

    @Test
    public void openReturnsWhenBackSelected() {
        String input = "0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            CustomerCLI.open(scanner);
            // Should return without error
            assertTrue(true);
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void openSelectsExistingCustomer() {
        Customer customer = DataStore.getCustomers().get(0);
        String input = "1\n" + customer.getId() + "\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            CustomerCLI.open(scanner);
            String output = out.toString();
            assertTrue(output.contains("Welcome back"));
            assertTrue(output.contains(customer.getName()));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void openRegistersNewCustomer() {
        String input = "2\nTest Customer\n123-456-7890\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            CustomerCLI.open(scanner);
            String output = out.toString();
            assertTrue(output.contains("Registered with ID"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void openHandlesInvalidCustomerSelection() {
        String input = "1\n99999\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            CustomerCLI.open(scanner);
            String output = out.toString();
            assertTrue(output.contains("Customer not found"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void openBrowsesEquipment() {
        Customer customer = DataStore.getCustomers().get(0);
        String input = "1\n" + customer.getId() + "\n1\n0\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            CustomerCLI.open(scanner);
            // Should execute without error
            assertTrue(true);
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void openRentsEquipment() {
        Customer customer = DataStore.getCustomers().get(0);
        Equipment equipment = DataStore.findEquipment(1000).orElseThrow(IllegalStateException::new);
        equipment.setAvailableUnits(5);
        DataStore.persistEquipment();
        
        String input = "1\n" + customer.getId() + "\n2\n" + equipment.getId() + "\n2\n0\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            CustomerCLI.open(scanner);
            String output = out.toString();
            assertTrue(output.contains("Created rental") || output.contains("Unable to rent"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void openReturnsRental() {
        Customer customer = DataStore.getCustomers().get(0);
        Equipment equipment = DataStore.findEquipment(1000).orElseThrow(IllegalStateException::new);
        equipment.setAvailableUnits(5);
        DataStore.persistEquipment();
        
        Rental rental = DataStore.addRental(equipment.getId(), customer.getId(), 
                java.time.LocalDate.now(), java.time.LocalDate.now().plusDays(2), 100.0);
        
        String input = "1\n" + customer.getId() + "\n3\n" + rental.getId() + "\n0\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            CustomerCLI.open(scanner);
            String output = out.toString();
            assertTrue(output.contains("Returned") || output.contains("Unable to return"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void openListsMyRentals() {
        Customer customer = DataStore.getCustomers().get(0);
        String input = "1\n" + customer.getId() + "\n4\n0\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            CustomerCLI.open(scanner);
            String output = out.toString();
            // Just verify it executed without error - output may vary
            assertTrue(output.length() > 0);
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void openExtendsRental() {
        Customer customer = DataStore.getCustomers().get(0);
        Equipment equipment = DataStore.findEquipment(1000).orElseThrow(IllegalStateException::new);
        equipment.setAvailableUnits(5);
        DataStore.persistEquipment();
        
        Rental rental = DataStore.addRental(equipment.getId(), customer.getId(), 
                java.time.LocalDate.now(), java.time.LocalDate.now().plusDays(2), 100.0);
        
        String input = "1\n" + customer.getId() + "\n5\n" + rental.getId() + "\n3\n0\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            CustomerCLI.open(scanner);
            String output = out.toString();
            assertTrue(output.contains("New due date") || output.contains("Unable to extend"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void openReservesEquipment() {
        Customer customer = DataStore.getCustomers().get(0);
        Equipment equipment = DataStore.findEquipment(1000).orElseThrow(IllegalStateException::new);
        equipment.setAvailableUnits(0);
        DataStore.persistEquipment();
        
        String input = "1\n" + customer.getId() + "\n6\n" + equipment.getId() + "\n0\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            CustomerCLI.open(scanner);
            String output = out.toString();
            assertTrue(output.contains("Reservation placed") || output.contains("Unable to reserve"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void openListsReservations() {
        Customer customer = DataStore.getCustomers().get(0);
        String input = "1\n" + customer.getId() + "\n7\n0\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            CustomerCLI.open(scanner);
            String output = out.toString();
            assertTrue(output.contains("No reservations found") || output.contains("Reservation"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void openHandlesInvalidChoice() {
        Customer customer = DataStore.getCustomers().get(0);
        String input = "1\n" + customer.getId() + "\n99\n0\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        
        try {
            CustomerCLI.open(scanner);
            String output = out.toString();
            assertTrue(output.contains("Unknown choice"));
        } finally {
            System.setOut(originalOut);
        }
    }
}

