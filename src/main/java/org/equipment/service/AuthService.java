package org.equipment.service;

import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private final Map<String, String> managerPins = new HashMap<>();

    public AuthService() {
        managerPins.put("admin", "1234");
        managerPins.put("lead", "4321");
    }

    public boolean verify(String username, String pin) {
        if (username == null || pin == null) {
            return false;
        }
        String stored = managerPins.get(username.trim().toLowerCase());
        return pin.equals(stored);
    }
}

