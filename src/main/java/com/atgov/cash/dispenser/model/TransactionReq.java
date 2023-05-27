package com.atgov.cash.dispenser.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import static com.atgov.cash.dispenser.constants.CashDispenserConstants.MIN_TRANSACTION_AMOUNT;

/**
 * This class data related to transaction request.
 */
public class TransactionReq {

    @NotNull
    @Min(value = MIN_TRANSACTION_AMOUNT, message = "Invalid amount: transaction amount should be greater " +
            "than or equal to " + MIN_TRANSACTION_AMOUNT)
    int amount;

    public int getAmount() {
        return amount;
    }
}
