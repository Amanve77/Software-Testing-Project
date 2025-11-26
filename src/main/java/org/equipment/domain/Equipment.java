package org.equipment.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Equipment {
    private final int id;
    private final String name;
    private final String category;
    private double dailyRate;
    private double depositAmount;
    private int availableUnits;
    private boolean underMaintenance;

    @JsonCreator
    public Equipment(
            @JsonProperty("id") int id,
            @JsonProperty("name") String name,
            @JsonProperty("category") String category,
            @JsonProperty("dailyRate") double dailyRate,
            @JsonProperty("depositAmount") double depositAmount,
            @JsonProperty("availableUnits") int availableUnits,
            @JsonProperty("underMaintenance") boolean underMaintenance) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.dailyRate = dailyRate;
        this.depositAmount = depositAmount;
        this.availableUnits = availableUnits;
        this.underMaintenance = underMaintenance;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(double dailyRate) {
        this.dailyRate = dailyRate;
    }

    public double getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(double depositAmount) {
        this.depositAmount = depositAmount;
    }

    public int getAvailableUnits() {
        return availableUnits;
    }

    public void setAvailableUnits(int availableUnits) {
        this.availableUnits = availableUnits;
    }

    public boolean isUnderMaintenance() {
        return underMaintenance;
    }

    public void setUnderMaintenance(boolean underMaintenance) {
        this.underMaintenance = underMaintenance;
    }

    @Override
    public String toString() {
        return id + " | " + name + " | " + category + " | rate: " + dailyRate + " | deposit: " + depositAmount
                + " | stock: " + availableUnits + (underMaintenance ? " | maintenance" : "");
    }
}

