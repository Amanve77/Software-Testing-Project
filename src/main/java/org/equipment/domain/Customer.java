package org.equipment.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Customer {
    private final int id;
    private final String name;
    private final String phone;

    @JsonCreator
    public Customer(
            @JsonProperty("id") int id,
            @JsonProperty("name") String name,
            @JsonProperty("phone") String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return id + " | " + name + " | " + phone;
    }
}

