package com.atgov.cash.dispenser.util;

import com.atgov.cash.dispenser.exception.CashDispenserValidationException;
import com.atgov.cash.dispenser.model.CashDispenser;
import com.atgov.cash.dispenser.model.Currency;

import java.util.Arrays;
import java.util.Map;

import static com.atgov.cash.dispenser.constants.CashDispenserConstants.BILL_TYPE_FIFTY;
import static com.atgov.cash.dispenser.constants.CashDispenserConstants.BILL_TYPE_TWENTY;
import static com.atgov.cash.dispenser.constants.CashDispenserConstants.CURRENT_SUPPORTED_BILL_TYPES;

/**
 * This class holds utility methods that can be reused when required.
 */
public class Util {

    public static void validateProvidedBills(Map<Currency, Integer> currencyRegister, boolean isInitializingPhase)
            throws CashDispenserValidationException {
        if (currencyRegister.size() > CURRENT_SUPPORTED_BILL_TYPES) {
            throw new CashDispenserValidationException("Invalid input data. " +
                    "Found more bill types than supported  " + CURRENT_SUPPORTED_BILL_TYPES + ".");
        } else {
            validateCurrencyTypes(currencyRegister);
        }
        Integer numOfTwentyBills = currencyRegister.get(new Currency(BILL_TYPE_TWENTY));
        Integer numOfFiftyBills = currencyRegister.get(new Currency(BILL_TYPE_FIFTY));
        if (isInitializingPhase && numOfTwentyBills == null && numOfFiftyBills == null) {
            throw new CashDispenserValidationException("Invalid input data. " +
                    "Could not find correct data for bill type " + BILL_TYPE_TWENTY + " and bill type " +
                    BILL_TYPE_FIFTY + ".Please prove data in the format {\"20\": <numOfBills>, \"50\": <numOfBills>}");
        } else if (!isInitializingPhase && numOfTwentyBills == null && numOfFiftyBills == null) {
            throw new CashDispenserValidationException("Invalid input data. " +
                    "Could not find correct data for bill type " + BILL_TYPE_TWENTY + " or bill type " +
                    BILL_TYPE_FIFTY + ".Please prove either or both data in the format " +
                    "{\"20\": <numOfBills>, \"50\": <numOfBills>}");
        }

        if (numOfTwentyBills != null && numOfTwentyBills <= 0) {
            throw new CashDispenserValidationException("Invalid input data. " +
                    "Could not find correct data for bill type " + BILL_TYPE_TWENTY + "." +
                    "Please provide number of bills greater than zero.");
        } else if (numOfFiftyBills != null && numOfFiftyBills <= 0) {
            throw new CashDispenserValidationException("Invalid input data. " +
                    "Could not find correct data for bill type " + BILL_TYPE_FIFTY + "." +
                    "Please provide number of bills greater than zero.");
        }
    }

    private static void validateCurrencyTypes(Map<Currency, Integer> currencyMap)
            throws CashDispenserValidationException {
        for (Currency currency : currencyMap.keySet()) {
            if (!isValidCurrencyType(currency)) {
                throw new CashDispenserValidationException("Invalid currency type provided " +
                        currency.getType() + ".Supported types are " + Arrays.asList(CurrencyType.values()));
            }
        }
    }

    private static boolean isValidCurrencyType(Currency currency) {
        for (CurrencyType currencyType : CurrencyType.values()) {
            if (currencyType.name().equalsIgnoreCase(currency.getType())) {
                return true;
            }
        }
        return false;
    }

    public static void calculateAndSetTotalValue(CashDispenser cashDispenser) {
        for (CurrencyType currencyType : CurrencyType.values()) {
            switch (currencyType) {
                case FIFTY :
                    cashDispenser.setTotalAmount(cashDispenser.getTotalAmount() + 50 *
                            cashDispenser.getBills().get(new Currency(currencyType.name())));
                    break;
                case TWENTY:
                    cashDispenser.setTotalAmount(cashDispenser.getTotalAmount() + 20 *
                            cashDispenser.getBills().get(new Currency(currencyType.name())));
            }
        }
    }
}
