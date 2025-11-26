package org.equipment.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.equipment.data.DataStore;
import org.equipment.domain.Equipment;
import org.equipment.domain.Reservation;
import org.equipment.support.TestDataSupport;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ReservationServiceTest {
    private final ReservationService service = new ReservationService();

    @BeforeClass
    public static void setupStore() {
        TestDataSupport.initStore();
    }

    @Before
    public void resetStore() {
        TestDataSupport.resetStore();
    }

    @Test
    public void listAllReturnsAllReservations() {
        List<Reservation> all = service.listAll();
        int initialSize = all.size();
        
        Equipment equipment = DataStore.findEquipment(1000).orElseThrow(IllegalStateException::new);
        equipment.setAvailableUnits(0);
        DataStore.persistEquipment();
        service.requestReservation(equipment.getId(), 5000);
        
        List<Reservation> afterAdd = service.listAll();
        assertEquals(initialSize + 1, afterAdd.size());
    }

    @Test
    public void listWaitingReturnsOnlyWaitingReservations() {
        Equipment equipment = DataStore.findEquipment(1000).orElseThrow(IllegalStateException::new);
        equipment.setAvailableUnits(0);
        DataStore.persistEquipment();
        
        Reservation reservation = service.requestReservation(equipment.getId(), 5000);
        List<Reservation> waiting = service.listWaiting();
        
        assertTrue(waiting.contains(reservation));
        assertTrue(waiting.stream().allMatch(r -> r.getStatus() == Reservation.Status.WAITING));
    }

    @Test
    public void listByCustomerReturnsOnlyCustomerReservations() {
        Equipment equipment = DataStore.findEquipment(1000).orElseThrow(IllegalStateException::new);
        equipment.setAvailableUnits(0);
        DataStore.persistEquipment();
        
        Reservation reservation = service.requestReservation(equipment.getId(), 5000);
        List<Reservation> customerReservations = service.listByCustomer(5000);
        
        assertTrue(customerReservations.contains(reservation));
        assertTrue(customerReservations.stream().allMatch(r -> r.getCustomerId() == 5000));
    }

    @Test(expected = IllegalStateException.class)
    public void requestReservationRejectsInStockEquipment() {
        service.requestReservation(1000, 5000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void requestReservationRejectsNonExistentEquipment() {
        service.requestReservation(9999, 5000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void requestReservationRejectsNonExistentCustomer() {
        Equipment equipment = DataStore.findEquipment(1000).orElseThrow(IllegalStateException::new);
        equipment.setAvailableUnits(0);
        DataStore.persistEquipment();
        service.requestReservation(equipment.getId(), 9999);
    }

    @Test
    public void requestReservationWhenOutOfStock() {
        Equipment camera = DataStore.findEquipment(1000)
                .orElseThrow(IllegalStateException::new);
        camera.setAvailableUnits(0);
        DataStore.persistEquipment();

        Reservation reservation = service.requestReservation(camera.getId(), 5000);
        assertEquals(Reservation.Status.WAITING, reservation.getStatus());
    }

    @Test
    public void requestReservationWhenUnderMaintenance() {
        Equipment equipment = DataStore.findEquipment(1000).orElseThrow(IllegalStateException::new);
        equipment.setAvailableUnits(5);
        equipment.setUnderMaintenance(true);
        DataStore.persistEquipment();

        Reservation reservation = service.requestReservation(equipment.getId(), 5000);
        assertEquals(Reservation.Status.WAITING, reservation.getStatus());
    }

    @Test
    public void declineReservationMarksStatus() {
        Equipment laptop = DataStore.findEquipment(1001).orElseThrow(IllegalStateException::new);
        laptop.setAvailableUnits(0);
        DataStore.persistEquipment();
        Reservation reservation = service.requestReservation(laptop.getId(), 5000);
        assertTrue(service.declineReservation(reservation.getId(), "manager:1"));
        assertEquals(Reservation.Status.DECLINED,
                DataStore.findReservation(reservation.getId()).get().getStatus());
    }

    @Test
    public void declineReservationReturnsFalseForNonExistentReservation() {
        assertFalse(service.declineReservation(99999, "manager:1"));
    }

    @Test
    public void markFulfilledMarksStatus() {
        Equipment equipment = DataStore.findEquipment(1000).orElseThrow(IllegalStateException::new);
        equipment.setAvailableUnits(0);
        DataStore.persistEquipment();
        Reservation reservation = service.requestReservation(equipment.getId(), 5000);
        
        assertTrue(service.markFulfilled(reservation.getId(), "manager:1"));
        assertEquals(Reservation.Status.FULFILLED,
                DataStore.findReservation(reservation.getId()).get().getStatus());
    }

    @Test
    public void markFulfilledReturnsFalseForNonExistentReservation() {
        assertFalse(service.markFulfilled(99999, "manager:1"));
    }
}


