package org.equipment.service;

import java.util.List;
import java.util.stream.Collectors;

import org.equipment.data.AuditLog;
import org.equipment.data.DataStore;
import org.equipment.domain.Customer;
import org.equipment.domain.Equipment;
import org.equipment.domain.Reservation;

public class ReservationService {

    public List<Reservation> listAll() {
        return DataStore.getReservations();
    }

    public List<Reservation> listWaiting() {
        return DataStore.getReservations().stream()
                .filter(r -> r.getStatus() == Reservation.Status.WAITING)
                .collect(Collectors.toList());
    }

    public List<Reservation> listByCustomer(int customerId) {
        return DataStore.getReservations().stream()
                .filter(r -> r.getCustomerId() == customerId)
                .collect(Collectors.toList());
    }

    public Reservation requestReservation(int equipmentId, int customerId) {
        Equipment equipment = DataStore.findEquipment(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found."));
        Customer customer = DataStore.findCustomer(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found."));

        if (!equipment.isUnderMaintenance() && equipment.getAvailableUnits() > 0) {
            throw new IllegalStateException("Equipment currently available; please rent directly.");
        }

        Reservation created = DataStore.addReservation(equipment.getId(), customer.getId());
        AuditLog.write("customer:" + customer.getId(), "Requested reservation " + created.getId());
        return created;
    }

    public boolean declineReservation(int reservationId, String actor) {
        return DataStore.findReservation(reservationId)
                .map(res -> {
                    res.setStatus(Reservation.Status.DECLINED);
                    DataStore.persistReservations();
                    AuditLog.write(actor, "Declined reservation " + reservationId);
                    return true;
                })
                .orElse(false);
    }

    public boolean markFulfilled(int reservationId, String actor) {
        return DataStore.findReservation(reservationId)
                .map(res -> {
                    res.setStatus(Reservation.Status.FULFILLED);
                    DataStore.persistReservations();
                    AuditLog.write(actor, "Fulfilled reservation " + reservationId);
                    return true;
                })
                .orElse(false);
    }
}

