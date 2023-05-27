package com.atgov.cash.dispenser.service;

import com.atgov.cash.dispenser.exception.CashDispenserValidationException;
import com.atgov.cash.dispenser.model.CashDispenser;
import com.atgov.cash.dispenser.model.Currency;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.atgov.cash.dispenser.constants.CashDispenserConstants.BILL_TYPE_FIFTY;
import static com.atgov.cash.dispenser.constants.CashDispenserConstants.BILL_TYPE_TWENTY;
import static com.atgov.cash.dispenser.constants.CashDispenserConstants.CURRENCY_CODE;
import static com.atgov.cash.dispenser.constants.CashDispenserConstants.DEFAULT_NUMBER_OF_FIFTY_BILLS;
import static com.atgov.cash.dispenser.constants.CashDispenserConstants.DEFAULT_NUMBER_OF_TWENTY_BILLS;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CashDispenserServiceTests {

    //todo need to implement more tests for
    // - Add or remove bills
    // - Transactions
    // - validating corner cases etc
    private CashDispenserService cashDispenserService;

    @BeforeEach
    public void setup() {
        cashDispenserService = new CashDispenserServiceImpl();
    }

    @DisplayName("=======Test for initializing cash dispenser and validating inputs======")
    @Test
    public void givenRandomInputs_whenInitialized_thenReturnCashDispenserObject()
            throws CashDispenserValidationException {
        CashDispenserServiceImpl.setIsInitialized(false);
        CashDispenser cashDispenser = cashDispenserService.init(500, 100, "AUD");

        Assertions.assertThat(cashDispenser).isNotNull();
        Assertions.assertThat(cashDispenser.getTotalAmount()).isEqualTo(15000);
        Assertions.assertThat(cashDispenser.getCurrencyCode()).isEqualTo("AUD");
    }

    @DisplayName("=======Test for initializing cash dispenser with default values=======")
    @Test
    public void givenNotNullInputs_whenInitialized_thenReturnCashDispenserObjectWithDefaultValues()
            throws CashDispenserValidationException {
        //Note: According application logic this default initialization phase will never occur
        CashDispenserServiceImpl.setIsInitialized(false);
        CashDispenser cashDispenser = cashDispenserService.init(0, 0, "");

        Assertions.assertThat(cashDispenser).isNotNull();
        Assertions.assertThat(cashDispenser.getTotalAmount()).isEqualTo((50 *
                DEFAULT_NUMBER_OF_FIFTY_BILLS) + (20 * DEFAULT_NUMBER_OF_TWENTY_BILLS));
        Assertions.assertThat(cashDispenser.getCurrencyCode()).isEqualTo(CURRENCY_CODE);
    }

    @DisplayName("=======Test for reinitialization validation======")
    @Test
    public void givenNotNullInputs_whenReInitialized_thenReturnError()
            throws CashDispenserValidationException {
        CashDispenserServiceImpl.setIsInitialized(false);
        cashDispenserService.init(200, 50, "AUD");
        CashDispenserValidationException exception = org.junit.jupiter.api.Assertions.
                assertThrows(CashDispenserValidationException.class, () -> {
                    cashDispenserService.init(500, 220, "AUD");
                });
        //todo use a constant when using custom error messages
        assertEquals("Cash Dispenser already initialized.Cannot run reinitialization." +
                "Use patch api to add/remove bills.", exception.getMessage());
    }

    @DisplayName("======Test for initializing cash dispenser and getting all available cash======")
    @Test
    public void givenRandomInputs_whenInitialized_thenReturnAllAvailableCash()
            throws CashDispenserValidationException {
        CashDispenserServiceImpl.setIsInitialized(false);
        cashDispenserService.init(5200, 340, "AUD");
        Map<Currency, Integer> mapOfCurrencies = cashDispenserService.retrieveAllCash();
        assertEquals(5200, mapOfCurrencies.get(new Currency(BILL_TYPE_TWENTY)));
        assertEquals(340, mapOfCurrencies.get(new Currency(BILL_TYPE_FIFTY)));
    }
}
