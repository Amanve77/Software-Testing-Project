package org.equipment.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.equipment.data.AuditLog;
import org.equipment.data.DataStore;
import org.equipment.domain.Customer;
import org.equipment.domain.Equipment;
import org.equipment.domain.Rental;
import org.equipment.domain.Reservation;

public class RentalService {
    private static final int MAX_ACTIVE_RENTALS_PER_CATEGORY = 3;

    public List<Rental> listAll() {
        return DataStore.getRentals();
    }

    public List<Rental> listByCustomer(int customerId) {
        return listAll().stream()
                .filter(r -> r.getCustomerId() == customerId)
                .collect(Collectors.toList());
    }

    public Optional<Rental> find(int rentalId) {
        return DataStore.findRental(rentalId);
    }

    public Rental rent(int equipmentId, int customerId, int days) {
        validateDuration(days);
        Equipment equipment = DataStore.findEquipment(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found."));
        ensureRentable(equipment);

        if (equipment.getAvailableUnits() <= 0) {
            throw new IllegalStateException("Equipment unavailable.");
        }

        Customer customer = DataStore.findCustomer(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found."));

        return createRental(equipment, customer, days, "customer:" + customer.getId());
    }

    public Rental fulfillReservation(int reservationId, int days, String actor) {
        validateDuration(days);
        Reservation reservation = DataStore.findReservation(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found."));
        if (reservation.getStatus() != Reservation.Status.WAITING) {
            throw new IllegalStateException("Reservation is not waiting.");
        }
        Equipment equipment = DataStore.findEquipment(reservation.getEquipmentId())
                .orElseThrow(() -> new IllegalArgumentException("Equipment missing."));
        ensureRentable(equipment);
        if (equipment.getAvailableUnits() <= 0) {
            throw new IllegalStateException("No stock available to fulfill reservation.");
        }
        Customer customer = DataStore.findCustomer(reservation.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer missing."));

        Rental rental = createRental(equipment, customer, days, actor);
        reservation.setStatus(Reservation.Status.FULFILLED);
        DataStore.persistReservations();
        return rental;
    }

    public double returnRental(int rentalId) {
        Rental rental = DataStore.findRental(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found."));

        if (rental.isReturned()) {
            return rental.getLateFeeCharged();
        }

        Equipment equipment = DataStore.findEquipment(rental.getEquipmentId())
                .orElseThrow(() -> new IllegalStateException("Equipment missing from catalog."));

        equipment.setAvailableUnits(equipment.getAvailableUnits() + 1);
        DataStore.persistEquipment();
        LocalDate returnDate = LocalDate.now();
        rental.setReturnedOn(returnDate);

        double fee = calculateLateFee(equipment, rental, returnDate);
        rental.setLateFeeCharged(fee);
        DataStore.persistRentals();
        AuditLog.write("customer:" + rental.getCustomerId(), "Returned rental " + rental.getId() + " fee=" + fee);
        return fee;
    }

    public Rental extendRental(int rentalId, int extraDays) {
        validateDuration(extraDays);
        Rental rental = DataStore.findRental(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found."));
        if (rental.isReturned()) {
            throw new IllegalStateException("Returned rentals cannot be extended.");
        }
        rental.setDueDate(rental.getDueDate().plusDays(extraDays));
        DataStore.persistRentals();
        AuditLog.write("customer:" + rental.getCustomerId(),
                "Extended rental " + rental.getId() + " by " + extraDays + " days");
        return rental;
    }

    public double totalHeldDeposits() {
        return DataStore.getRentals().stream()
                .filter(r -> !r.isReturned())
                .mapToDouble(Rental::getDepositAmount)
                .sum();
    }

    public List<Rental> overdueRentals() {
        LocalDate today = LocalDate.now();
        return DataStore.getRentals().stream()
                .filter(r -> !r.isReturned() && r.getDueDate().isBefore(today))
                .collect(Collectors.toList());
    }

    public double projectedLateFee(Rental rental) {
        Equipment equipment = DataStore.findEquipment(rental.getEquipmentId())
                .orElseThrow(() -> new IllegalStateException("Equipment missing from catalog."));
        return calculateLateFee(equipment, rental, LocalDate.now());
    }

    private void validateDuration(int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("Rental days must be positive.");
        }
    }

    private void ensureRentable(Equipment equipment) {
        if (equipment.isUnderMaintenance()) {
            throw new IllegalStateException("Equipment is under maintenance.");
        }
        enforceCategoryCapacity(equipment);
    }

    private Rental createRental(Equipment equipment, Customer customer, int days, String actor) {
        equipment.setAvailableUnits(equipment.getAvailableUnits() - 1);
        DataStore.persistEquipment();

        LocalDate start = LocalDate.now();
        LocalDate due = start.plusDays(days);
        double deposit = equipment.getDepositAmount() > 0 ? equipment.getDepositAmount() : equipment.getDailyRate();
        Rental rental = DataStore.addRental(equipment.getId(), customer.getId(), start, due, deposit);
        AuditLog.write(actor, "Created rental " + rental.getId() + " for equipment " + equipment.getId());
        return rental;
    }

    private void enforceCategoryCapacity(Equipment requestEquipment) {
        long activeInCategory = DataStore.getRentals().stream()
                .filter(r -> !r.isReturned())
                .map(r -> DataStore.findEquipment(r.getEquipmentId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(eq -> eq.getCategory().equalsIgnoreCase(requestEquipment.getCategory()))
                .count();
        if (activeInCategory >= MAX_ACTIVE_RENTALS_PER_CATEGORY) {
            throw new IllegalStateException("Category capacity reached for " + requestEquipment.getCategory());
        }
    }

    private double calculateLateFee(Equipment equipment, Rental rental, LocalDate returnDate) {
        if (returnDate.isAfter(rental.getDueDate())) {
            long overdueDays = ChronoUnit.DAYS.between(rental.getDueDate(), returnDate);
            return overdueDays * equipment.getDailyRate() * 1.25;
        }
        return 0.0;
    }
}

