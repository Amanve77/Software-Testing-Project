package org.equipment.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.equipment.data.DataStore;
import org.equipment.domain.Customer;
import org.equipment.support.TestDataSupport;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CustomerServiceTest {
    private final CustomerService service = new CustomerService();

    @BeforeClass
    public static void setupStore() {
        TestDataSupport.initStore();
    }

    @Before
    public void resetStore() {
        TestDataSupport.resetStore();
    }

    @Test
    public void registerNormalizesInputAndPersists() {
        Customer created = service.register("  Mira  Rao ", "999-555-8888");
        assertEquals("Mira  Rao", created.getName());
        assertEquals("999-555-8888", created.getPhone());
        assertTrue(DataStore.getCustomers().stream().anyMatch(c -> c.getId() == created.getId()));
    }

    @Test
    public void findReturnsKnownCustomer() {
        Customer existing = DataStore.getCustomers().get(0);
        assertEquals(existing.getId(), service.find(existing.getId()).get().getId());
    }
}



