package org.equipment.domain;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.Test;

public class RentalAndReservationTest {

    @Test
    public void rentalReturnedFlagAndToString() {
        Rental r = new Rental(8001, 1000, 5000, LocalDate.now(), LocalDate.now().plusDays(2), 10.0);
        assertFalse(r.isReturned());
        r.setReturnedOn(LocalDate.now());
        assertTrue(r.isReturned());
        r.setLateFeeCharged(3.5);
        assertEquals(3.5, r.getLateFeeCharged(), 0.0001);
        assertTrue(r.toString().contains("lateFee"));
    }

    @Test
    public void reservationStatusChangeAndToString() {
        Reservation res = new Reservation(9001, 1000, 5000, LocalDate.now(), Reservation.Status.WAITING);
        assertEquals(Reservation.Status.WAITING, res.getStatus());
        res.setStatus(Reservation.Status.APPROVED);
        assertEquals(Reservation.Status.APPROVED, res.getStatus());
        assertTrue(res.toString().contains("WAITING") || res.toString().contains("APPROVED"));
    }
}
