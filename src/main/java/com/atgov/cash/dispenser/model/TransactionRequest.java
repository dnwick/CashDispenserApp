package com.atgov.cash.dispenser.model;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import static com.atgov.cash.dispenser.constants.CashDispenserConstants.MIN_TRANSACTION_AMOUNT;

/**
 * This class data related to transaction request.
 */
public class TransactionRequest {

    @NotNull
    @Min(value = MIN_TRANSACTION_AMOUNT, message = "Invalid amount: Transaction amount should be greater " +
            "than or equal to " + MIN_TRANSACTION_AMOUNT)
    @Digits(integer = 10, fraction = 0, message = "Invalid amount: Amount must be a whole number")
    int amount;

    public int getAmount() {
        return amount;
    }
}
