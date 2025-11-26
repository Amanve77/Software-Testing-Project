package org.equipment.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.equipment.data.DataStore;
import org.equipment.domain.Equipment;
import org.equipment.support.TestDataSupport;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class InventoryServiceTest {
    private final InventoryService service = new InventoryService();

    @BeforeClass
    public static void setupStore() {
        TestDataSupport.initStore();
    }

    @Before
    public void resetStore() {
        TestDataSupport.resetStore();
    }

    @Test
    public void addEquipmentValidatesAndPersists() {
        Equipment created = service.addEquipment("Sony FX6", "Camera", 55.0, 200.0, 2, false);
        assertTrue(DataStore.getEquipment().stream().anyMatch(eq -> eq.getId() == created.getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addEquipmentRejectsZeroRate() {
        service.addEquipment("Bad", "Camera", 0, 100, 1, false);
    }

    @Test
    public void updateRateStockAndMaintenance() {
        Equipment original = DataStore.getEquipment().get(0);
        assertTrue(service.updateRate(original.getId(), 99.0));
        assertTrue(service.updateStock(original.getId(), 10));
        assertTrue(service.flipMaintenance(original.getId()));

        Equipment reloaded = DataStore.findEquipment(original.getId()).orElseThrow(IllegalStateException::new);
        assertEquals(99.0, reloaded.getDailyRate(), 0.0001);
        assertEquals(10, reloaded.getAvailableUnits());
        assertTrue(reloaded.isUnderMaintenance());
    }

    @Test
    public void lowStockReportFiltersThreshold() {
        List<Equipment> low = service.lowStockReport(3);
        assertEquals(2, low.size()); // IDs 1000 (3) and 1002 (2)
    }
}



