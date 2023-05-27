package com.atgov.cash.dispenser.service;

import com.atgov.cash.dispenser.exception.CashDispenserValidationException;
import com.atgov.cash.dispenser.model.CashDispenser;
import com.atgov.cash.dispenser.model.Currency;

import java.util.Map;

/**
 * Interface define the contract of cash dispenser service.
 */
public interface CashDispenserService {

    CashDispenser init(int numberOfTwentyBills, int numberOfFiftyBills, String currencyCode)
            throws CashDispenserValidationException;

    Map<Currency, Integer> retrieveAllCash();

    Map<Currency, Integer> addCashToDispenser(Map<Currency, Integer> newCurrencies)
            throws CashDispenserValidationException;

    Map<Currency, Integer> removeCashFromDispenser(Map<Currency, Integer> newCurrencies)
            throws CashDispenserValidationException;

    Map<Currency, Integer> dispenseBills(int amount) throws CashDispenserValidationException;
}
