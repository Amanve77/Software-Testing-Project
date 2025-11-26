package org.equipment.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.equipment.data.DataStore;
import org.equipment.domain.Customer;
import org.equipment.domain.Equipment;
import org.equipment.domain.Rental;

public class ReportService {

    public double totalHeldDeposits() {
        return DataStore.getRentals().stream()
                .filter(r -> !r.isReturned())
                .mapToDouble(Rental::getDepositAmount)
                .sum();
    }

    public double totalLateFeesCollected() {
        return DataStore.getRentals().stream()
                .mapToDouble(Rental::getLateFeeCharged)
                .sum();
    }

    public Map<String, Long> overdueByCategory() {
        LocalDate today = LocalDate.now();
        return DataStore.getRentals().stream()
                .filter(r -> !r.isReturned() && r.getDueDate().isBefore(today))
                .collect(Collectors.groupingBy(
                        rental -> DataStore.findEquipment(rental.getEquipmentId())
                                .map(Equipment::getCategory)
                                .orElse("Unknown"),
                        Collectors.counting()));
    }

    public List<CustomerSummary> topCustomersByRentals(int limit) {
        Map<Integer, Long> counts = DataStore.getRentals().stream()
                .collect(Collectors.groupingBy(Rental::getCustomerId, Collectors.counting()));

        return counts.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(limit)
                .map(entry -> {
                    Customer customer = DataStore.findCustomer(entry.getKey())
                            .orElse(new Customer(entry.getKey(), "Unknown", ""));
                    return new CustomerSummary(customer.getId(), customer.getName(), entry.getValue());
                })
                .collect(Collectors.toList());
    }

    public static class CustomerSummary {
        private final int customerId;
        private final String name;
        private final long rentals;

        public CustomerSummary(int customerId, String name, long rentals) {
            this.customerId = customerId;
            this.name = name;
            this.rentals = rentals;
        }

        public int getCustomerId() {
            return customerId;
        }

        public String getName() {
            return name;
        }

        public long getRentals() {
            return rentals;
        }

        @Override
        public String toString() {
            return name + " (" + customerId + ") -> " + rentals + " rentals";
        }
    }
}

