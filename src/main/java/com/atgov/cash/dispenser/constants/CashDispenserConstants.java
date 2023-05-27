package com.atgov.cash.dispenser.constants;

/**
 * Class to hold constants.
 */
public final class CashDispenserConstants {

    public static final int DEFAULT_NUMBER_OF_TWENTY_BILLS = 100;
    public static final int DEFAULT_NUMBER_OF_FIFTY_BILLS = 50;
    public static final int MIN_TRANSACTION_AMOUNT = 20;
    public static final int MAX_TRANSACTION_AMOUNT = 10000;
    public static final String BILL_TYPE_FIFTY = "FIFTY";
    public static final int BILL_TYPE_FIFTY_VALUE = 50;
    public static final String BILL_TYPE_TWENTY = "TWENTY";
    public static final int BILL_TYPE_TWENTY_VALUE = 20;
    public static final String CURRENCY_CODE = "AUD";
    public static final int CURRENT_SUPPORTED_BILL_TYPES = 2;
    public static final String OP_ADD = "add";
    public static final String OP_REMOVE = "remove";
}
