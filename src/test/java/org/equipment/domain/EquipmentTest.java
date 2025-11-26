package org.equipment.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class EquipmentTest {

    @Test
    public void gettersAndSettersAndToString() {
        Equipment e = new Equipment(9999, "TestCam", "Camera", 10.0, 5.0, 2, false);
        assertEquals(9999, e.getId());
        assertEquals("TestCam", e.getName());
        assertEquals("Camera", e.getCategory());
        assertEquals(10.0, e.getDailyRate(), 0.0001);
        e.setDailyRate(12.3);
        assertEquals(12.3, e.getDailyRate(), 0.0001);

        e.setAvailableUnits(7);
        assertEquals(7, e.getAvailableUnits());

        e.setUnderMaintenance(true);
        assertTrue(e.isUnderMaintenance());

        String s = e.toString();
        assertTrue(s.contains("TestCam"));
        assertTrue(s.contains("stock: 7"));
    }
}
