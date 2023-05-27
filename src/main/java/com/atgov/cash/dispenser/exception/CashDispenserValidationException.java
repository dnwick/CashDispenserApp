package com.atgov.cash.dispenser.exception;

/**
 * Exception class used to throw errors related to cash dispenser functionality.
 */
public class CashDispenserValidationException extends Exception {

    public CashDispenserValidationException(String message) {
        super(message);
    }

    public CashDispenserValidationException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public CashDispenserValidationException(Throwable throwable) {
        super(throwable);
    }
}
