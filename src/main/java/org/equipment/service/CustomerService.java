package org.equipment.service;

import java.util.List;
import java.util.Optional;

import org.equipment.data.DataStore;
import org.equipment.domain.Customer;
import org.equipment.utils.ValidationUtils;

public class CustomerService {

    public List<Customer> listCustomers() {
        return DataStore.getCustomers();
    }

    public Customer register(String name, String phone) {
        String normalizedName = ValidationUtils.normalizeName(name);
        String normalizedPhone = ValidationUtils.normalizePhone(phone);
        return DataStore.addCustomer(normalizedName, normalizedPhone);
    }

    public Optional<Customer> find(int customerId) {
        return DataStore.findCustomer(customerId);
    }
}

