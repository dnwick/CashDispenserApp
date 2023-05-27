package com.atgov.cash.dispenser.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

/**
 * This class hold data related to cash dispenser.
 */

public class CashDispenser {

    @NotNull
    @NotEmpty(message = "Invalid currencies: No currencies provided with number of each currency")
    private Map<Currency, Integer> bills;
    private int totalAmount;

    @NotNull
    @NotBlank(message = "Invalid Currency Code: Empty currencyCode")
    private String currencyCode;
    private List<Transaction> transactions;

    public CashDispenser(Map<Currency, Integer> bills, int totalAmount, String currencyCode,
                         List<Transaction> transactions) {
        this.bills = bills;
        this.totalAmount = totalAmount;
        this.currencyCode = currencyCode;
        this.transactions = transactions;
    }

    public Map<Currency, Integer> getBills() {
        return bills;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        return "CashDispenser{" +
                "bills=" + bills +
                ", totalAmount=" + totalAmount +
                ", currencyCode='" + currencyCode + '\'' +
                ", transactions=" + transactions +
                '}';
    }
}
