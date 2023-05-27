package com.atgov.cash.dispenser.resource;

import com.atgov.cash.dispenser.exception.CashDispenserValidationException;
import com.atgov.cash.dispenser.model.CashDispenser;
import com.atgov.cash.dispenser.model.Currency;
import com.atgov.cash.dispenser.model.TransactionReq;
import com.atgov.cash.dispenser.model.UpdateBill;
import com.atgov.cash.dispenser.service.CashDispenserService;
import com.atgov.cash.dispenser.service.CashDispenserServiceImpl;
import com.atgov.cash.dispenser.util.Util;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.atgov.cash.dispenser.constants.CashDispenserConstants.BILL_TYPE_FIFTY;
import static com.atgov.cash.dispenser.constants.CashDispenserConstants.BILL_TYPE_TWENTY;
import static com.atgov.cash.dispenser.constants.CashDispenserConstants.OP_ADD;
import static com.atgov.cash.dispenser.constants.CashDispenserConstants.OP_REMOVE;

/**
 * This class act as the controller/resource for cash dispenser functionality.
 */
@RestController
public class CashInventoryResource {

    private CashDispenserService service;

    public CashInventoryResource(CashDispenserServiceImpl cashDispenserServiceImpl) {
        this.service = cashDispenserServiceImpl;
    }

    @RequestMapping(value = "/v1/cash-inventory", method = RequestMethod.POST)
    public ResponseEntity<CashDispenser> initializeDispenser(@Valid @RequestBody CashDispenser cashDispenser)
            throws CashDispenserValidationException {
        // todo validate for a threshold of allowed number of bills from each bill
        // todo validate for providing number of bills in other data types
        //Assuming when initializing input need to have all the supported bill types
        Util.validateProvidedBills(cashDispenser.getBills(), true);
        cashDispenser = service.init(cashDispenser.getBills().get(new Currency(BILL_TYPE_TWENTY)),
                cashDispenser.getBills().get(new Currency(BILL_TYPE_FIFTY)), cashDispenser.getCurrencyCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(cashDispenser);
    }

    @RequestMapping("/v1/cash-inventory/bills")
    public Map<Currency, Integer> retrieveAllCash() {
        return service.retrieveAllCash();
    }

    @RequestMapping(value = "/v1/cash-inventory", method = RequestMethod.PATCH,
            consumes = "application/json-patch+json")
    public Map<Currency, Integer> modifyBills(@Valid @RequestBody UpdateBill updateBillReq)
            throws CashDispenserValidationException {
        Util.validateProvidedBills(updateBillReq.getValue(), false);
        switch (updateBillReq.getOperation().toLowerCase()) {
            case OP_ADD:
                return service.addCashToDispenser(updateBillReq.getValue());
            case OP_REMOVE:
                return service.removeCashFromDispenser(updateBillReq.getValue());
            default:
                throw new CashDispenserValidationException("Operation provided " + updateBillReq.getOperation() +
                        " is not supported");
        }
    }

    @RequestMapping(value = "/v1/cash-inventory/transactions", method = RequestMethod.POST)
    public Map<Currency, Integer> doTransactions(@Valid @RequestBody TransactionReq transactionReq)
            throws CashDispenserValidationException {
        return service.dispenseBills(transactionReq.getAmount());
    }
}
