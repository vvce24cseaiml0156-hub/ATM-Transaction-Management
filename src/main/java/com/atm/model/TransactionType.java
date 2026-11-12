package com.atm.model;

public enum TransactionType {
    BALANCE_ENQUIRY("Balance Enquiry"),
    WITHDRAWAL("Cash Withdrawal"),
    DEPOSIT("Cash Deposit");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
