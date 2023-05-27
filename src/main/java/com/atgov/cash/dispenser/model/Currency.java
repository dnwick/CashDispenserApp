package com.atgov.cash.dispenser.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * This class hold data related to specific currency.
 */
public class Currency {

    @NotNull
    @NotBlank(message = "Invalid Currency type: Empty currency type")
    private String type;

    public Currency(String type) {
        super();
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Currency)) {
            return false;
        }
        Currency currency = (Currency) o;
        return type.equals(currency.type);
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    public String getType() {
        return type;
    }
}
