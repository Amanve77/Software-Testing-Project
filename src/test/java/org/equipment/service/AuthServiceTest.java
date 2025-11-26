package org.equipment.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AuthServiceTest {
    private final AuthService service = new AuthService();

    @Test
    public void verifiesCaseInsensitiveAdmins() {
        assertTrue(service.verify("Admin", "1234"));
        assertTrue(service.verify("lead", "4321"));
    }

    @Test
    public void rejectsMissingOrWrongCredentials() {
        assertFalse(service.verify(null, "1234"));
        assertFalse(service.verify("admin", null));
        assertFalse(service.verify("admin", "9999"));
        assertFalse(service.verify("unknown", "1234"));
    }
}



