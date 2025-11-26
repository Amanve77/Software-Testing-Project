package org.equipment.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.equipment.domain.Customer;
import org.equipment.domain.Equipment;
import org.equipment.domain.Rental;
import org.equipment.domain.Reservation;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * JSON-backed store that keeps deterministic defaults for tests while persisting
 * user changes into the local data directory.
 */
public final class DataStore {
    private static final Path DATA_DIR = Paths
            .get(System.getProperty("equipment.data.dir", "data"));
    private static final Path EQUIPMENT_FILE = DATA_DIR.resolve("equipment.json");
    private static final Path CUSTOMERS_FILE = DATA_DIR.resolve("customers.json");
    private static final Path RENTALS_FILE = DATA_DIR.resolve("rentals.json");
    private static final Path RESERVATIONS_FILE = DATA_DIR.resolve("reservations.json");

    private static final TypeReference<List<Equipment>> EQUIPMENT_TYPE = new TypeReference<List<Equipment>>() {
    };
    private static final TypeReference<List<Customer>> CUSTOMERS_TYPE = new TypeReference<List<Customer>>() {
    };
    private static final TypeReference<List<Rental>> RENTALS_TYPE = new TypeReference<List<Rental>>() {
    };
    private static final TypeReference<List<Reservation>> RESERVATIONS_TYPE = new TypeReference<List<Reservation>>() {
    };

    private static final List<Equipment> EQUIPMENT = new ArrayList<>();
    private static final List<Customer> CUSTOMERS = new ArrayList<>();
    private static final List<Rental> RENTALS = new ArrayList<>();
    private static final List<Reservation> RESERVATIONS = new ArrayList<>();

    private static final AtomicInteger EQUIPMENT_SEQ = new AtomicInteger(1000);
    private static final AtomicInteger CUSTOMER_SEQ = new AtomicInteger(5000);
    private static final AtomicInteger RENTAL_SEQ = new AtomicInteger(9000);
    private static final AtomicInteger RESERVATION_SEQ = new AtomicInteger(12000);

    private static boolean seeded = false;

    private DataStore() {
    }

    public static synchronized void seed() {
        if (seeded) {
            return;
        }
        loadOrSeed();
        AuditLog.configure(DATA_DIR);
        seeded = true;
    }

    private static void loadOrSeed() {
        EQUIPMENT.clear();
        CUSTOMERS.clear();
        RENTALS.clear();
        RESERVATIONS.clear();

        EQUIPMENT.addAll(JsonStore.read(EQUIPMENT_FILE, EQUIPMENT_TYPE, defaultEquipment()));
        CUSTOMERS.addAll(JsonStore.read(CUSTOMERS_FILE, CUSTOMERS_TYPE, defaultCustomers()));
        RENTALS.addAll(JsonStore.read(RENTALS_FILE, RENTALS_TYPE, defaultRentals()));
        RESERVATIONS.addAll(JsonStore.read(RESERVATIONS_FILE, RESERVATIONS_TYPE, new ArrayList<>()));

        EQUIPMENT_SEQ.set(nextSeq(EQUIPMENT.stream().mapToInt(Equipment::getId).max().orElse(999)));
        CUSTOMER_SEQ.set(nextSeq(CUSTOMERS.stream().mapToInt(Customer::getId).max().orElse(4999)));
        RENTAL_SEQ.set(nextSeq(RENTALS.stream().mapToInt(Rental::getId).max().orElse(8999)));
        RESERVATION_SEQ.set(nextSeq(RESERVATIONS.stream().mapToInt(Reservation::getId).max().orElse(11999)));

        persistAll();
    }

    private static List<Equipment> defaultEquipment() {
        List<Equipment> defaults = new ArrayList<>();
        defaults.add(new Equipment(EQUIPMENT_SEQ.getAndIncrement(), "Canon EOS 90D", "Camera", 42.5, 150.0, 3, false));
        defaults.add(new Equipment(EQUIPMENT_SEQ.getAndIncrement(), "Bosch Hammer Drill", "Tool", 18.0, 75.0, 5, false));
        defaults.add(new Equipment(EQUIPMENT_SEQ.getAndIncrement(), "DJI Ronin-S", "Stabilizer", 27.0, 120.0, 2, true));
        return defaults;
    }

    private static List<Customer> defaultCustomers() {
        List<Customer> defaults = new ArrayList<>();
        defaults.add(new Customer(CUSTOMER_SEQ.getAndIncrement(), "Asha Gupta", "999-111-2222"));
        defaults.add(new Customer(CUSTOMER_SEQ.getAndIncrement(), "Mark Patel", "999-333-4444"));
        return defaults;
    }

    private static List<Rental> defaultRentals() {
        List<Rental> defaults = new ArrayList<>();
        if (!EQUIPMENT.isEmpty() && !CUSTOMERS.isEmpty()) {
            defaults.add(new Rental(
                    RENTAL_SEQ.getAndIncrement(),
                    EQUIPMENT.get(0).getId(),
                    CUSTOMERS.get(0).getId(),
                    LocalDate.now().minusDays(2),
                    LocalDate.now().plusDays(3),
                    EQUIPMENT.get(0).getDepositAmount()));
        }
        return defaults;
    }

    private static int nextSeq(int currentMax) {
        return currentMax + 1;
    }

    private static void persistAll() {
        JsonStore.write(EQUIPMENT_FILE, EQUIPMENT);
        JsonStore.write(CUSTOMERS_FILE, CUSTOMERS);
        JsonStore.write(RENTALS_FILE, RENTALS);
        JsonStore.write(RESERVATIONS_FILE, RESERVATIONS);
    }

    public static List<Equipment> getEquipment() {
        return Collections.unmodifiableList(EQUIPMENT);
    }

    public static List<Customer> getCustomers() {
        return Collections.unmodifiableList(CUSTOMERS);
    }

    public static List<Rental> getRentals() {
        return Collections.unmodifiableList(RENTALS);
    }

    public static List<Reservation> getReservations() {
        return Collections.unmodifiableList(RESERVATIONS);
    }

    public static Equipment addEquipment(String name, String category, double dailyRate, double depositAmount, int stock,
            boolean maintenance) {
        Equipment created = new Equipment(EQUIPMENT_SEQ.getAndIncrement(), name, category, dailyRate, depositAmount,
                stock, maintenance);
        EQUIPMENT.add(created);
        persistAll();
        return created;
    }

    public static Customer addCustomer(String name, String phone) {
        Customer created = new Customer(CUSTOMER_SEQ.getAndIncrement(), name, phone);
        CUSTOMERS.add(created);
        persistAll();
        return created;
    }

    public static Rental addRental(int equipmentId, int customerId, LocalDate start, LocalDate due, double deposit) {
        Rental created = new Rental(RENTAL_SEQ.getAndIncrement(), equipmentId, customerId, start, due, deposit);
        RENTALS.add(created);
        persistAll();
        return created;
    }

    public static Reservation addReservation(int equipmentId, int customerId) {
        Reservation created = new Reservation(RESERVATION_SEQ.getAndIncrement(), equipmentId, customerId, LocalDate.now(),
                Reservation.Status.WAITING);
        RESERVATIONS.add(created);
        persistAll();
        return created;
    }

    public static void persistEquipment() {
        JsonStore.write(EQUIPMENT_FILE, EQUIPMENT);
    }

    public static void persistRentals() {
        JsonStore.write(RENTALS_FILE, RENTALS);
    }

    public static void persistReservations() {
        JsonStore.write(RESERVATIONS_FILE, RESERVATIONS);
    }

    public static void reset() {
        try {
            Files.deleteIfExists(EQUIPMENT_FILE);
            Files.deleteIfExists(CUSTOMERS_FILE);
            Files.deleteIfExists(RENTALS_FILE);
            Files.deleteIfExists(RESERVATIONS_FILE);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to reset data files: " + ex.getMessage(), ex);
        }
        seeded = false;
        EQUIPMENT_SEQ.set(1000);
        CUSTOMER_SEQ.set(5000);
        RENTAL_SEQ.set(9000);
        RESERVATION_SEQ.set(12000);
        loadOrSeed();
        seeded = true;
    }

    public static Optional<Equipment> findEquipment(int id) {
        return EQUIPMENT.stream().filter(eq -> eq.getId() == id).findFirst();
    }

    public static Optional<Customer> findCustomer(int id) {
        return CUSTOMERS.stream().filter(c -> c.getId() == id).findFirst();
    }

    public static Optional<Rental> findRental(int id) {
        return RENTALS.stream().filter(r -> r.getId() == id).findFirst();
    }

    public static Optional<Reservation> findReservation(int id) {
        return RESERVATIONS.stream().filter(r -> r.getId() == id).findFirst();
    }
}
