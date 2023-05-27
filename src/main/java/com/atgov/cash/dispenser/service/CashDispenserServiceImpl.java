package com.atgov.cash.dispenser.service;

import com.atgov.cash.dispenser.exception.CashDispenserValidationException;
import com.atgov.cash.dispenser.model.CashDispenser;
import com.atgov.cash.dispenser.model.Currency;
import com.atgov.cash.dispenser.model.Transaction;
import com.atgov.cash.dispenser.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.atgov.cash.dispenser.constants.CashDispenserConstants.BILL_TYPE_FIFTY;
import static com.atgov.cash.dispenser.constants.CashDispenserConstants.BILL_TYPE_FIFTY_VALUE;
import static com.atgov.cash.dispenser.constants.CashDispenserConstants.BILL_TYPE_TWENTY;
import static com.atgov.cash.dispenser.constants.CashDispenserConstants.BILL_TYPE_TWENTY_VALUE;
import static com.atgov.cash.dispenser.constants.CashDispenserConstants.CURRENCY_CODE;
import static com.atgov.cash.dispenser.constants.CashDispenserConstants.DEFAULT_NUMBER_OF_FIFTY_BILLS;
import static com.atgov.cash.dispenser.constants.CashDispenserConstants.DEFAULT_NUMBER_OF_TWENTY_BILLS;
import static com.atgov.cash.dispenser.constants.CashDispenserConstants.MAX_TRANSACTION_AMOUNT;

/**
 * This class holds implementation of cash inventory resource.
 */
@Service
public class CashDispenserServiceImpl implements CashDispenserService {

    private static final Logger log = LogManager.getLogger(CashDispenserServiceImpl.class);
    private static CashDispenser cashDispenser = new CashDispenser(new HashMap<>(), 0, "",
            new ArrayList<>());
    private static boolean isInitialized = false;

    public static void setIsInitialized(boolean isInitialized) {
        CashDispenserServiceImpl.isInitialized = isInitialized;
    }

    @Override
    public CashDispenser init(int numberOfTwentyBills, int numberOfFiftyBills, String currencyCode)
            throws CashDispenserValidationException {
        if (!isInitialized) {
            int twentyBillAmount = numberOfTwentyBills == 0 ? DEFAULT_NUMBER_OF_TWENTY_BILLS : numberOfTwentyBills;
            int fiftyBillAmount = numberOfFiftyBills == 0 ? DEFAULT_NUMBER_OF_FIFTY_BILLS : numberOfFiftyBills;
            String code = currencyCode == null || currencyCode.isEmpty() ? CURRENCY_CODE : currencyCode;

            Map<Currency, Integer> currencyRegister = cashDispenser.getBills();
            currencyRegister.put(new Currency(BILL_TYPE_FIFTY), fiftyBillAmount);
            currencyRegister.put(new Currency(BILL_TYPE_TWENTY), twentyBillAmount);

            cashDispenser.setCurrencyCode(code);
            cashDispenser.setTotalAmount(0);
            Util.calculateAndSetTotalValue(cashDispenser);

            log.debug("Cash dispenser initialized with total value of " + cashDispenser.getTotalAmount()
                    + " " + code + " comprise of " + numberOfFiftyBills + " fifty " + code + " bills and " +
                    numberOfTwentyBills + " twenty " + code + " bills.");
            isInitialized = true;
        } else {
            throw new CashDispenserValidationException("Cash Dispenser already initialized." +
                    "Cannot run reinitialization.Use patch api to add/remove bills.");
        }
        return cashDispenser;
    }

    @Override
    public Map<Currency, Integer> retrieveAllCash() {
        log.debug("Cash dispenser retrieve all cash method called which returning " +
                cashDispenser.getBills());
        return cashDispenser.getBills();
    }

    @Override
    public Map<Currency, Integer> addCashToDispenser(Map<Currency, Integer> newCurrencies)
            throws CashDispenserValidationException {
        if (isInitialized) {
            for (Map.Entry<Currency, Integer> currency : cashDispenser.getBills().entrySet()) {
                Integer existingValue = currency.getValue();
                Integer newValue = newCurrencies.get(currency.getKey());
                if (newValue != null) {
                    cashDispenser.getBills().put(currency.getKey(), existingValue + newValue);
                    log.debug("Increased the number of bills for bill type " + currency.getKey() +
                            " from " + existingValue + " to " + existingValue + newValue);
                }
            }
            Util.calculateAndSetTotalValue(cashDispenser);
        } else {
            throw new CashDispenserValidationException("Cash Dispenser is not initialized." +
                    "Please initialize it first before add more bills.");
        }
        return cashDispenser.getBills();
    }

    @Override
    public Map<Currency, Integer> removeCashFromDispenser(Map<Currency, Integer> newCurrencies)
            throws CashDispenserValidationException {
        //todo we can try to merge add and remove cash methods to promote code reuse
        if (isInitialized) {
            for (Map.Entry<Currency, Integer> currency : cashDispenser.getBills().entrySet()) {
                Integer existingValue = currency.getValue();
                Integer newValue = newCurrencies.get(currency.getKey());
                if (newValue != null) {
                    int updatedValue = existingValue - newValue;
                    if (updatedValue <= 0) {
                        throw new CashDispenserValidationException("Cannot remove bills of type " + currency.getKey() +
                                " as proceeding this action will remove all " + currency.getKey() + " bills." +
                                "Please remove while keeping considerable amount in the dispenser." +
                                "Currently dispenser contains " + existingValue + " of " + currency.getKey() +
                                " bill type.");
                    }
                    cashDispenser.getBills().put(currency.getKey(), updatedValue);
                    log.debug("Decreased the number of bills for bill type " + currency.getKey() +
                            " from " + existingValue + " to " + updatedValue);
                }
            }
            Util.calculateAndSetTotalValue(cashDispenser);
        } else {
            throw new CashDispenserValidationException("Cash Dispenser is not initialized." +
                    "Please initialize it first before remove bills.");
        }
        return cashDispenser.getBills();
    }

    @Override
    public Map<Currency, Integer> dispenseBills(int requestedAmount) throws CashDispenserValidationException {
        if (isInitialized) {
            Map<Currency, Integer> dispensableBills = new HashMap<>();
            if (cashDispenser.getTotalAmount() >= requestedAmount) {
                if (requestedAmount <= MAX_TRANSACTION_AMOUNT) {
                    // Perform the logic to dispense the bills based on legal combinations

                    // Dispense the requested amount with $50 and $20 bills if possible
                    int possible50BillCount = requestedAmount / BILL_TYPE_FIFTY_VALUE;
                    int remainingAmount = requestedAmount % BILL_TYPE_FIFTY_VALUE;
                    int possible20BillCount = 0;
                    if (remainingAmount != 0) {
                        possible20BillCount = remainingAmount / BILL_TYPE_TWENTY_VALUE;
                    }
                    boolean isRemainingAmountCanBeSatisfiedWith20Bills = remainingAmount %
                            BILL_TYPE_TWENTY_VALUE == 0;

                    if (!isRemainingAmountCanBeSatisfiedWith20Bills) {
                        //Since remaining amount cannot be satisfied with 50 and 20 bill try with only 20 bill
                        possible50BillCount = 0;
                        if (requestedAmount % BILL_TYPE_TWENTY_VALUE == 0) {
                            possible20BillCount = requestedAmount / BILL_TYPE_TWENTY_VALUE;
                            isRemainingAmountCanBeSatisfiedWith20Bills = true;
                        }
                    }

                    if (isRemainingAmountCanBeSatisfiedWith20Bills) {
                        // Legal combination of notes found, return the dispensed bills
                        Currency fiftyBill = new Currency(BILL_TYPE_FIFTY);
                        Currency twentyBill = new Currency(BILL_TYPE_TWENTY);
                        int available20Bills = cashDispenser.getBills().get(twentyBill);
                        int available50Bills = cashDispenser.getBills().get(fiftyBill);
                        if (possible50BillCount > 0 && available50Bills >= possible50BillCount) {
                            dispensableBills.put(fiftyBill, possible50BillCount);
                            cashDispenser.getBills().put(fiftyBill, available50Bills - possible50BillCount);
                        }
                        if (possible20BillCount != 0 && available20Bills >= possible20BillCount) {
                            dispensableBills.put(twentyBill, possible20BillCount);
                            cashDispenser.getBills().put(twentyBill, available20Bills - possible20BillCount);
                        }
                        Util.calculateAndSetTotalValue(cashDispenser);
                        cashDispenser.getTransactions().add(new Transaction(UUID.randomUUID().toString(),
                                cashDispenser.getTotalAmount(), System.currentTimeMillis()));
                        log.debug("Dispensed " + possible50BillCount + " " + BILL_TYPE_FIFTY + " bills and " +
                                possible20BillCount + " " + BILL_TYPE_TWENTY + " bills of total " +
                                cashDispenser.getTotalAmount() + " " + cashDispenser.getCurrencyCode() + ".");
                        return dispensableBills;
                    } else {
                        throw new CashDispenserValidationException("Unable to dispense the requested amount " +
                                requestedAmount + " with legal combinations of bill types " + BILL_TYPE_FIFTY +
                                " and " + BILL_TYPE_TWENTY +
                                ".");
                    }
                } else {
                    throw new CashDispenserValidationException("Transaction limit reached.Requested amount " +
                            requestedAmount + " is higher than the limit per transaction " + MAX_TRANSACTION_AMOUNT);
                }
            } else {
                throw new CashDispenserValidationException("Unable to dispense the requested amount " +
                        requestedAmount + " due to low on physical notes available in the cash dispenser.");
            }
        } else {
            throw new CashDispenserValidationException("Cash Dispenser need to be initialized first before " +
                    "dispensing bills.");
        }
    }


}
