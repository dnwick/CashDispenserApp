package com.atgov.cash.dispenser.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * This class hold data related to partial updates.
 */
public class UpdateBill {

    @NotNull
    @NotBlank(message = "Invalid operation type: Empty operation type")
    private String operation;

    @NotNull
    @NotBlank(message = "Invalid key type: Empty key type")
    private String key;

    @NotNull
    @NotEmpty(message = "Invalid currencies: No currencies provided with number of each currency")
    private Map<Currency, Integer> value;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<Currency, Integer> getValue() {
        return value;
    }

    public void setValue(Map<Currency, Integer> value) {
        this.value = value;
    }
}
