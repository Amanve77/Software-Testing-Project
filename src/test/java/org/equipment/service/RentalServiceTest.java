package org.equipment.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.equipment.data.DataStore;
import org.equipment.domain.Customer;
import org.equipment.domain.Equipment;
import org.equipment.domain.Rental;
import org.equipment.domain.Reservation;
import org.equipment.support.TestDataSupport;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RentalServiceTest {
    private final RentalService rentalService = new RentalService();
    private final ReservationService reservationService = new ReservationService();

    @BeforeClass
    public static void setupStore() {
        TestDataSupport.initStore();
    }

    @Before
    public void resetStore() {
        TestDataSupport.resetStore();
    }

    @Test
    public void listAllReturnsAllRentals() {
        List<Rental> all = rentalService.listAll();
        assertTrue(all.size() >= 0);
        
        Rental rental = rentalService.rent(1000, 5000, 2);
        List<Rental> afterAdd = rentalService.listAll();
        assertTrue(afterAdd.contains(rental));
    }

    @Test
    public void listByCustomerReturnsOnlyCustomerRentals() {
        Rental rental1 = rentalService.rent(1000, 5000, 2);
        Rental rental2 = rentalService.rent(1001, 5001, 2);
        
        List<Rental> customerRentals = rentalService.listByCustomer(5000);
        assertTrue(customerRentals.contains(rental1));
        assertFalse(customerRentals.contains(rental2));
        assertTrue(customerRentals.stream().allMatch(r -> r.getCustomerId() == 5000));
    }

    @Test
    public void findReturnsRentalWhenExists() {
        Rental rental = rentalService.rent(1000, 5000, 2);
        Optional<Rental> found = rentalService.find(rental.getId());
        assertTrue(found.isPresent());
        assertEquals(rental.getId(), found.get().getId());
    }

    @Test
    public void findReturnsEmptyWhenNotExists() {
        Optional<Rental> found = rentalService.find(99999);
        assertFalse(found.isPresent());
    }

    @Test
    public void rentDecrementsStockAndHoldsDeposit() {
        Equipment equipment = DataStore.findEquipment(1000).orElseThrow(IllegalStateException::new);
        int initialStock = equipment.getAvailableUnits();

        Rental rental = rentalService.rent(equipment.getId(), 5000, 2);

        assertEquals(initialStock - 1,
                DataStore.findEquipment(equipment.getId()).get().getAvailableUnits());
        assertEquals(equipment.getDepositAmount(), rental.getDepositAmount(), 0.0001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rentRejectsInvalidDuration() {
        rentalService.rent(1000, 5000, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rentRejectsNegativeDuration() {
        rentalService.rent(1000, 5000, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rentRejectsNonExistentEquipment() {
        rentalService.rent(9999, 5000, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rentRejectsNonExistentCustomer() {
        rentalService.rent(1000, 9999, 2);
    }

    @Test(expected = IllegalStateException.class)
    public void rentRejectsWhenOutOfStock() {
        Equipment equipment = DataStore.findEquipment(1000).orElseThrow(IllegalStateException::new);
        equipment.setAvailableUnits(0);
        DataStore.persistEquipment();
        rentalService.rent(equipment.getId(), 5000, 2);
    }

    @Test(expected = IllegalStateException.class)
    public void rentRejectsWhenUnderMaintenance() {
        Equipment equipment = DataStore.findEquipment(1000).orElseThrow(IllegalStateException::new);
        equipment.setUnderMaintenance(true);
        DataStore.persistEquipment();
        rentalService.rent(equipment.getId(), 5000, 2);
    }

    @Test(expected = IllegalStateException.class)
    public void rentEnforcesCategoryCapacity() {
        Equipment equipment = DataStore.findEquipment(1000).orElseThrow(IllegalStateException::new);
        equipment.setAvailableUnits(5);
        DataStore.persistEquipment();
        Customer customer = DataStore.getCustomers().get(0);

        for (int i = 0; i < 3; i++) {
            Equipment extra = DataStore.addEquipment("Temp Camera " + i, equipment.getCategory(), 10 + i, 50, 1, false);
            DataStore.addRental(extra.getId(), customer.getId(), LocalDate.now().minusDays(1), LocalDate.now().plusDays(1),
                    extra.getDepositAmount());
        }

        rentalService.rent(equipment.getId(), customer.getId(), 1);
    }

    @Test
    public void extendRentalUpdatesDueDate() {
        Rental rental = rentalService.rent(1000, 5000, 2);
        LocalDate originalDue = rental.getDueDate();
        Rental extended = rentalService.extendRental(rental.getId(), 3);
        assertEquals(originalDue.plusDays(3), extended.getDueDate());
    }

    @Test(expected = IllegalArgumentException.class)
    public void extendRentalRejectsInvalidDuration() {
        Rental rental = rentalService.rent(1000, 5000, 2);
        rentalService.extendRental(rental.getId(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void extendRentalRejectsNonExistentRental() {
        rentalService.extendRental(99999, 3);
    }

    @Test(expected = IllegalStateException.class)
    public void extendRentalRejectsReturnedRental() {
        Rental rental = rentalService.rent(1000, 5000, 2);
        rentalService.returnRental(rental.getId());
        rentalService.extendRental(rental.getId(), 3);
    }

    @Test
    public void returnRentalCalculatesLateFee() {
        Rental rental = rentalService.rent(1000, 5000, 1);
        rental.setDueDate(LocalDate.now().minusDays(2));
        DataStore.persistRentals();

        double fee = rentalService.returnRental(rental.getId());
        assertTrue(fee > 0);
        assertEquals(fee, rentalService.returnRental(rental.getId()), 0.0001);
    }

    @Test
    public void returnRentalReturnsZeroFeeWhenOnTime() {
        Rental rental = rentalService.rent(1000, 5000, 5);
        rental.setDueDate(LocalDate.now().plusDays(1));
        DataStore.persistRentals();

        double fee = rentalService.returnRental(rental.getId());
        assertEquals(0.0, fee, 0.0001);
    }

    @Test
    public void returnRentalReturnsZeroFeeWhenEarly() {
        Rental rental = rentalService.rent(1000, 5000, 5);
        rental.setDueDate(LocalDate.now().plusDays(2));
        DataStore.persistRentals();

        double fee = rentalService.returnRental(rental.getId());
        assertEquals(0.0, fee, 0.0001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void returnRentalRejectsNonExistentRental() {
        rentalService.returnRental(99999);
    }

    @Test
    public void totalHeldDepositsReturnsSumOfActiveRentals() {
        Rental rental1 = rentalService.rent(1000, 5000, 2);
        Rental rental2 = rentalService.rent(1001, 5000, 2);
        
        double total = rentalService.totalHeldDeposits();
        assertTrue(total >= rental1.getDepositAmount() + rental2.getDepositAmount());
        
        rentalService.returnRental(rental1.getId());
        double afterReturn = rentalService.totalHeldDeposits();
        assertTrue(afterReturn < total);
    }

    @Test
    public void overdueRentalsReturnsOnlyOverdue() {
        Rental rental1 = rentalService.rent(1000, 5000, 2);
        Rental rental2 = rentalService.rent(1001, 5000, 2);
        rental1.setDueDate(LocalDate.now().minusDays(1));
        rental2.setDueDate(LocalDate.now().plusDays(1));
        DataStore.persistRentals();

        List<Rental> overdue = rentalService.overdueRentals();
        assertTrue(overdue.contains(rental1));
        assertFalse(overdue.contains(rental2));
    }

    @Test
    public void projectedLateFeeCalculatesCorrectly() {
        Rental rental = rentalService.rent(1000, 5000, 1);
        rental.setDueDate(LocalDate.now().minusDays(2));
        DataStore.persistRentals();

        double projected = rentalService.projectedLateFee(rental);
        assertTrue(projected > 0);
    }

    @Test
    public void projectedLateFeeReturnsZeroWhenNotOverdue() {
        Rental rental = rentalService.rent(1000, 5000, 5);
        rental.setDueDate(LocalDate.now().plusDays(1));
        DataStore.persistRentals();

        double projected = rentalService.projectedLateFee(rental);
        assertEquals(0.0, projected, 0.0001);
    }

    @Test
    public void projectedLateFeeWorksWithValidEquipment() {
        Rental rental = rentalService.rent(1000, 5000, 2);
        double fee = rentalService.projectedLateFee(rental);
        assertTrue(fee >= 0);
    }

    @Test
    public void fulfillReservationCreatesRental() {
        Equipment equipment = DataStore.findEquipment(1000).orElseThrow(IllegalStateException::new);
        equipment.setAvailableUnits(0);
        DataStore.persistEquipment();
        Reservation reservation = reservationService.requestReservation(equipment.getId(), 5000);
        equipment.setAvailableUnits(1);
        DataStore.persistEquipment();

        Rental rental = rentalService.fulfillReservation(reservation.getId(), 2, "manager:1");
        assertEquals(Reservation.Status.FULFILLED,
                DataStore.findReservation(reservation.getId()).get().getStatus());
        assertEquals(equipment.getId(), rental.getEquipmentId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void fulfillReservationRejectsInvalidDuration() {
        Equipment equipment = DataStore.findEquipment(1000).orElseThrow(IllegalStateException::new);
        equipment.setAvailableUnits(0);
        DataStore.persistEquipment();
        Reservation reservation = reservationService.requestReservation(equipment.getId(), 5000);
        equipment.setAvailableUnits(1);
        DataStore.persistEquipment();
        
        rentalService.fulfillReservation(reservation.getId(), 0, "manager:1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void fulfillReservationRejectsNonExistentReservation() {
        rentalService.fulfillReservation(99999, 2, "manager:1");
    }

    @Test(expected = IllegalStateException.class)
    public void fulfillReservationRejectsNonWaitingReservation() {
        Equipment equipment = DataStore.findEquipment(1000).orElseThrow(IllegalStateException::new);
        equipment.setAvailableUnits(0);
        DataStore.persistEquipment();
        Reservation reservation = reservationService.requestReservation(equipment.getId(), 5000);
        reservation.setStatus(Reservation.Status.DECLINED);
        DataStore.persistReservations();
        equipment.setAvailableUnits(1);
        DataStore.persistEquipment();
        
        rentalService.fulfillReservation(reservation.getId(), 2, "manager:1");
    }

    @Test(expected = IllegalStateException.class)
    public void fulfillReservationRejectsWhenOutOfStock() {
        Equipment equipment = DataStore.findEquipment(1000).orElseThrow(IllegalStateException::new);
        equipment.setAvailableUnits(0);
        DataStore.persistEquipment();
        Reservation reservation = reservationService.requestReservation(equipment.getId(), 5000);
        
        rentalService.fulfillReservation(reservation.getId(), 2, "manager:1");
    }

    @Test(expected = IllegalStateException.class)
    public void fulfillReservationRejectsWhenUnderMaintenance() {
        Equipment equipment = DataStore.findEquipment(1000).orElseThrow(IllegalStateException::new);
        equipment.setAvailableUnits(0);
        DataStore.persistEquipment();
        Reservation reservation = reservationService.requestReservation(equipment.getId(), 5000);
        equipment.setAvailableUnits(1);
        equipment.setUnderMaintenance(true);
        DataStore.persistEquipment();
        
        rentalService.fulfillReservation(reservation.getId(), 2, "manager:1");
    }
}



