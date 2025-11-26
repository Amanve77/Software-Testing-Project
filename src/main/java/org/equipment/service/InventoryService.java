package org.equipment.service;

import java.util.List;
import java.util.Optional;

import org.equipment.data.DataStore;
import org.equipment.domain.Equipment;

public class InventoryService {

    public List<Equipment> listAll() {
        return DataStore.getEquipment();
    }

    public Equipment addEquipment(String name, String category, double rate, double deposit, int stock,
            boolean maintenance) {
        validateRate(rate);
        validateDeposit(deposit);
        validateStock(stock);
        return DataStore.addEquipment(name, category, rate, deposit, stock, maintenance);
    }

    public Optional<Equipment> find(int equipmentId) {
        return DataStore.findEquipment(equipmentId);
    }

    public boolean updateRate(int equipmentId, double newRate) {
        validateRate(newRate);
        Optional<Equipment> equipment = find(equipmentId);
        equipment.ifPresent(eq -> {
            eq.setDailyRate(newRate);
            DataStore.persistEquipment();
        });
        return equipment.isPresent();
    }

    public boolean updateStock(int equipmentId, int newStock) {
        validateStock(newStock);
        Optional<Equipment> equipment = find(equipmentId);
        equipment.ifPresent(eq -> {
            eq.setAvailableUnits(newStock);
            DataStore.persistEquipment();
        });
        return equipment.isPresent();
    }

    public boolean flipMaintenance(int equipmentId) {
        Optional<Equipment> equipment = find(equipmentId);
        equipment.ifPresent(eq -> {
            eq.setUnderMaintenance(!eq.isUnderMaintenance());
            DataStore.persistEquipment();
        });
        return equipment.isPresent();
    }

    public List<Equipment> lowStockReport(int threshold) {
        return DataStore.getEquipment().stream()
                .filter(eq -> eq.getAvailableUnits() <= threshold)
                .collect(java.util.stream.Collectors.toList());
    }

    private void validateRate(double rate) {
        if (rate <= 0) {
            throw new IllegalArgumentException("Daily rate must be positive.");
        }
    }

    private void validateDeposit(double deposit) {
        if (deposit < 0) {
            throw new IllegalArgumentException("Deposit cannot be negative.");
        }
    }

    private void validateStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative.");
        }
    }
}

