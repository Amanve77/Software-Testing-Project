package org.equipment.domain;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Rental {
    private final int id;
    private final int equipmentId;
    private final int customerId;
    private final LocalDate startDate;
    private LocalDate dueDate;
    private final double depositAmount;
    private LocalDate returnedOn;
    private double lateFeeCharged;

    @JsonCreator
    public Rental(
            @JsonProperty("id") int id,
            @JsonProperty("equipmentId") int equipmentId,
            @JsonProperty("customerId") int customerId,
            @JsonProperty("startDate") LocalDate startDate,
            @JsonProperty("dueDate") LocalDate dueDate,
            @JsonProperty("depositAmount") double depositAmount) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.customerId = customerId;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.depositAmount = depositAmount;
    }

    public int getId() {
        return id;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public double getDepositAmount() {
        return depositAmount;
    }

    public LocalDate getReturnedOn() {
        return returnedOn;
    }

    public void setReturnedOn(LocalDate returnedOn) {
        this.returnedOn = returnedOn;
    }

    public boolean isReturned() {
        return returnedOn != null;
    }

    public double getLateFeeCharged() {
        return lateFeeCharged;
    }

    public void setLateFeeCharged(double lateFeeCharged) {
        this.lateFeeCharged = lateFeeCharged;
    }

    @Override
    public String toString() {
        return id + " | eq:" + equipmentId + " | cust:" + customerId + " | start:" + startDate + " | due:" + dueDate
                + " | deposit:" + depositAmount + (returnedOn != null ? " | returned:" + returnedOn : "")
                + (lateFeeCharged > 0 ? " | lateFee:" + lateFeeCharged : "");
    }
}
