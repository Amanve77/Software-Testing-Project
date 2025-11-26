package org.equipment.cli;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.equipment.data.AuditLog;
import org.equipment.domain.Equipment;
import org.equipment.domain.Reservation;
import org.equipment.domain.Rental;
import org.equipment.service.AuthService;
import org.equipment.service.InventoryService;
import org.equipment.service.ReportService;
import org.equipment.service.ReservationService;
import org.equipment.service.RentalService;

public final class ManagerCLI {
    private static final InventoryService INVENTORY = new InventoryService();
    private static final RentalService RENTALS = new RentalService();
    private static final ReservationService RESERVATIONS = new ReservationService();
    private static final ReportService REPORTS = new ReportService();
    private static final AuthService AUTH = new AuthService();

    private ManagerCLI() {
    }

    public static void open(Scanner scanner) {
        if (!authenticate(scanner)) {
            System.out.println("Access denied.");
            return;
        }

        boolean back = false;
        while (!back) {
            MainCLI.printDivider();
            System.out.println("Manager Portal");
            System.out.println("1. List inventory");
            System.out.println("2. Add equipment");
            System.out.println("3. Update daily rate");
            System.out.println("4. Toggle maintenance state");
            System.out.println("5. View rentals");
            System.out.println("6. Low stock report");
            System.out.println("7. Deposits & late fee summary");
            System.out.println("8. Reservation queue");
            System.out.println("9. Reports dashboard");
            System.out.println("10. Audit log");
            System.out.println("0. Back");
            System.out.print("Choice: ");

            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    listInventory();
                    break;
                case "2":
                    addEquipment(scanner);
                    break;
                case "3":
                    updateRate(scanner);
                    break;
                case "4":
                    toggleMaintenance(scanner);
                    break;
                case "5":
                    listRentals();
                    break;
                case "6":
                    lowStockReport(scanner);
                    break;
                case "7":
                    showFinancials();
                    break;
                case "8":
                    manageReservations(scanner);
                    break;
                case "9":
                    showReports();
                    break;
                case "10":
                    System.out.println(AuditLog.readAll());
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid selection.");
            }
        }
    }

    private static void listInventory() {
        INVENTORY.listAll().forEach(System.out::println);
    }

    private static void addEquipment(Scanner scanner) {
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Category: ");
        String category = scanner.nextLine();
        double rate = promptDouble(scanner, "Daily rate: ");
        double deposit = promptDouble(scanner, "Deposit amount: ");
        int stock = promptInt(scanner, "Initial stock: ");
        System.out.print("Under maintenance? (y/n): ");
        boolean maintenance = scanner.nextLine().trim().equalsIgnoreCase("y");

        Equipment equipment = INVENTORY.addEquipment(name, category, rate, deposit, stock, maintenance);
        System.out.println("Created " + equipment);
    }

    private static void updateRate(Scanner scanner) {
        int id = promptInt(scanner, "Equipment ID: ");
        double rate = promptDouble(scanner, "New rate: ");
        if (INVENTORY.updateRate(id, rate)) {
            System.out.println("Rate updated.");
        } else {
            System.out.println("Equipment not found.");
        }
    }

    private static void toggleMaintenance(Scanner scanner) {
        int id = promptInt(scanner, "Equipment ID: ");
        if (INVENTORY.flipMaintenance(id)) {
            System.out.println("Maintenance flag toggled.");
        } else {
            System.out.println("Equipment not found.");
        }
    }

    private static void listRentals() {
        RENTALS.listAll().forEach(System.out::println);
    }

    private static void lowStockReport(Scanner scanner) {
        int threshold = promptInt(scanner, "Alert when units <= ");
        List<Equipment> lowStock = INVENTORY.lowStockReport(threshold);
        if (lowStock.isEmpty()) {
            System.out.println("All items exceed the threshold.");
        } else {
            lowStock.forEach(eq -> System.out.println("[LOW] " + eq));
        }
    }

    private static void showFinancials() {
        double deposits = REPORTS.totalHeldDeposits();
        double fees = REPORTS.totalLateFeesCollected();
        System.out.println("Active deposits held: " + deposits);
        System.out.println("Late fees collected so far: " + fees);
        List<Rental> overdue = RENTALS.overdueRentals();
        if (overdue.isEmpty()) {
            System.out.println("No overdue rentals.");
        } else {
            System.out.println("Overdue rentals and projected late fees:");
            overdue.forEach(rental -> {
                double fee = RENTALS.projectedLateFee(rental);
                System.out.println(rental + " | projected late fee: " + fee);
            });
        }
    }

    private static void manageReservations(Scanner scanner) {
        while (true) {
            MainCLI.printDivider();
            List<Reservation> waiting = RESERVATIONS.listWaiting();
            if (waiting.isEmpty()) {
                System.out.println("No pending reservations.");
            } else {
                waiting.forEach(System.out::println);
            }
            System.out.println("1. Fulfill reservation");
            System.out.println("2. Decline reservation");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    fulfillReservation(scanner);
                    break;
                case "2":
                    declineReservation(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Unknown choice.");
            }
        }
    }

    private static void fulfillReservation(Scanner scanner) {
        int reservationId = promptInt(scanner, "Reservation ID: ");
        int days = promptInt(scanner, "Rental days: ");
        try {
            RENTALS.fulfillReservation(reservationId, days, "manager");
            System.out.println("Reservation fulfilled and rental created.");
        } catch (RuntimeException ex) {
            System.out.println("Unable to fulfill: " + ex.getMessage());
        }
    }

    private static void declineReservation(Scanner scanner) {
        int reservationId = promptInt(scanner, "Reservation ID: ");
        if (RESERVATIONS.declineReservation(reservationId, "manager")) {
            System.out.println("Reservation declined.");
        } else {
            System.out.println("Reservation not found.");
        }
    }

    private static void showReports() {
        MainCLI.printDivider();
        System.out.println("Overdue by category:");
        Map<String, Long> overdueMap = REPORTS.overdueByCategory();
        if (overdueMap.isEmpty()) {
            System.out.println("All clear.");
        } else {
            overdueMap.forEach((cat, count) -> System.out.println(cat + ": " + count));
        }

        System.out.println("\nTop customers:");
        REPORTS.topCustomersByRentals(5).forEach(summary -> System.out.println(summary));
    }

    private static boolean authenticate(Scanner scanner) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            System.out.print("Manager username: ");
            String username = scanner.nextLine();
            System.out.print("PIN: ");
            String pin = scanner.nextLine();
            if (AUTH.verify(username, pin)) {
                System.out.println("Welcome " + username + "!");
                return true;
            }
            System.out.println("Invalid credentials. Attempts left: " + (3 - attempt));
        }
        return false;
    }

    private static int promptInt(Scanner scanner, String label) {
        while (true) {
            System.out.print(label);
            try {
                int value = Integer.parseInt(scanner.nextLine());
                if (value <= 0) {
                    throw new NumberFormatException();
                }
                return value;
            } catch (NumberFormatException ex) {
                System.out.println("Enter a positive number.");
            }
        }
    }

    private static double promptDouble(Scanner scanner, String label) {
        while (true) {
            System.out.print(label);
            try {
                double value = Double.parseDouble(scanner.nextLine());
                if (value <= 0) {
                    throw new NumberFormatException();
                }
                return value;
            } catch (NumberFormatException ex) {
                System.out.println("Enter a positive decimal number.");
            }
        }
    }
}

