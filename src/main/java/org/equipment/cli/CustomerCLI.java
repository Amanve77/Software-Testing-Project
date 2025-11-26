package org.equipment.cli;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.equipment.domain.Customer;
import org.equipment.domain.Equipment;
import org.equipment.domain.Rental;
import org.equipment.domain.Reservation;
import org.equipment.service.CustomerService;
import org.equipment.service.InventoryService;
import org.equipment.service.RentalService;
import org.equipment.service.ReservationService;

public final class CustomerCLI {
    private static final CustomerService CUSTOMERS = new CustomerService();
    private static final InventoryService INVENTORY = new InventoryService();
    private static final RentalService RENTALS = new RentalService();
    private static final ReservationService RESERVATIONS = new ReservationService();

    private CustomerCLI() {
    }

    public static void open(Scanner scanner) {
        Optional<Customer> current = selectCustomer(scanner);
        if (!current.isPresent()) {
            return;
        }

        boolean back = false;
        while (!back) {
            MainCLI.printDivider();
            System.out.println("Customer Portal (" + current.get().getName() + ")");
            System.out.println("1. Browse available equipment");
            System.out.println("2. Rent equipment");
            System.out.println("3. Return rental");
            System.out.println("4. My rentals");
            System.out.println("5. Extend rental");
            System.out.println("6. Reserve equipment");
            System.out.println("7. My reservations");
            System.out.println("0. Back");
            System.out.print("Choice: ");

            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    browseEquipment();
                    break;
                case "2":
                    rentEquipment(scanner, current.get());
                    break;
                case "3":
                    returnRental(scanner);
                    break;
                case "4":
                    listMyRentals(current.get());
                    break;
                case "5":
                    extendRental(scanner);
                    break;
                case "6":
                    reserveEquipment(scanner, current.get());
                    break;
                case "7":
                    listReservations(current.get());
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Unknown choice.");
            }
        }
    }

    private static Optional<Customer> selectCustomer(Scanner scanner) {
        while (true) {
            MainCLI.printDivider();
            System.out.println("1. Existing customer");
            System.out.println("2. Register new customer");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    int id = promptInt(scanner, "Customer ID: ");
                    Optional<Customer> existing = CUSTOMERS.find(id);
                    if (existing.isPresent()) {
                        System.out.println("Welcome back, " + existing.get().getName());
                        return existing;
                    } else {
                        System.out.println("Customer not found.");
                    }
                    break;
                case "2":
                    System.out.print("Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Phone: ");
                    String phone = scanner.nextLine();
                    Customer created = CUSTOMERS.register(name, phone);
                    System.out.println("Registered with ID " + created.getId());
                    return Optional.of(created);
                case "0":
                    return Optional.empty();
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void browseEquipment() {
        List<Equipment> equipment = INVENTORY.listAll();
        equipment.stream()
                .filter(eq -> !eq.isUnderMaintenance())
                .forEach(System.out::println);
    }

    private static void rentEquipment(Scanner scanner, Customer customer) {
        int equipmentId = promptInt(scanner, "Equipment ID: ");
        int days = promptInt(scanner, "Days: ");
        try {
            Rental rental = RENTALS.rent(equipmentId, customer.getId(), days);
            System.out.println("Created rental " + rental.getId() + ". Deposit held: " + rental.getDepositAmount());
        } catch (RuntimeException ex) {
            System.out.println("Unable to rent: " + ex.getMessage());
        }
    }

    private static void returnRental(Scanner scanner) {
        int rentalId = promptInt(scanner, "Rental ID: ");
        try {
            double fee = RENTALS.returnRental(rentalId);
            if (fee > 0) {
                System.out.println("Returned with late fee: " + fee);
            } else {
                System.out.println("Returned on time. Thank you!");
            }
        } catch (RuntimeException ex) {
            System.out.println("Unable to return: " + ex.getMessage());
        }
    }

    private static void extendRental(Scanner scanner) {
        int rentalId = promptInt(scanner, "Rental ID: ");
        int days = promptInt(scanner, "Extra days: ");
        try {
            Rental rental = RENTALS.extendRental(rentalId, days);
            System.out.println("New due date: " + rental.getDueDate());
        } catch (RuntimeException ex) {
            System.out.println("Unable to extend: " + ex.getMessage());
        }
    }

    private static void reserveEquipment(Scanner scanner, Customer customer) {
        int equipmentId = promptInt(scanner, "Equipment ID: ");
        try {
            Reservation reservation = RESERVATIONS.requestReservation(equipmentId, customer.getId());
            System.out.println("Reservation placed with ID " + reservation.getId());
        } catch (RuntimeException ex) {
            System.out.println("Unable to reserve: " + ex.getMessage());
        }
    }

    private static void listReservations(Customer customer) {
        List<Reservation> reservations = RESERVATIONS.listByCustomer(customer.getId());
        if (reservations.isEmpty()) {
            System.out.println("No reservations found.");
        } else {
            reservations.forEach(System.out::println);
        }
    }

    private static void listMyRentals(Customer customer) {
        List<Rental> rentals = RENTALS.listByCustomer(customer.getId());
        if (rentals.isEmpty()) {
            System.out.println("No rentals yet.");
        } else {
            rentals.forEach(System.out::println);
        }
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
}

