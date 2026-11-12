package com.atm.exception;

public class AccountNotFoundException extends ATMException {
    public AccountNotFoundException(String accountNumber) {
        super("Account not found with account number: " + accountNumber);
    }
}
