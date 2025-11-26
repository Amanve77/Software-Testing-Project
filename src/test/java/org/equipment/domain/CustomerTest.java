package org.equipment.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class CustomerTest {

    @Test
    public void toStringAndGetters() {
        Customer c = new Customer(7777, "Zoe", "111-222-3333");
        assertEquals(7777, c.getId());
        assertEquals("Zoe", c.getName());
        assertEquals("111-222-3333", c.getPhone());

        String s = c.toString();
        assertTrue(s.contains("Zoe"));
        assertTrue(s.contains("7777"));
    }
}
