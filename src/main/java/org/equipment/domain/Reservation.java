package org.equipment.domain;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Reservation {
    public enum Status {
        WAITING,
        APPROVED,
        DECLINED,
        FULFILLED
    }

    private final int id;
    private final int equipmentId;
    private final int customerId;
    private final LocalDate requestedOn;
    private Status status;

    @JsonCreator
    public Reservation(
            @JsonProperty("id") int id,
            @JsonProperty("equipmentId") int equipmentId,
            @JsonProperty("customerId") int customerId,
            @JsonProperty("requestedOn") LocalDate requestedOn,
            @JsonProperty("status") Status status) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.customerId = customerId;
        this.requestedOn = requestedOn;
        this.status = status;
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

    public LocalDate getRequestedOn() {
        return requestedOn;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return id + " | eq:" + equipmentId + " | cust:" + customerId + " | on:" + requestedOn + " | " + status;
    }
}

