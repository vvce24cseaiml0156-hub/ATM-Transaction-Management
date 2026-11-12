package com.atm;

import com.atm.exception.AccountNotFoundException;
import com.atm.exception.InsufficientBalanceException;
import com.atm.exception.InvalidAmountException;
import com.atm.exception.InvalidPinException;
import com.atm.model.Account;
import com.atm.model.Transaction;
import com.atm.service.ATMService;

import java.util.List;

public class ATM {
    private final ATMService atmService;
    private Account currentSessionAccount;

    public ATM(ATMService atmService) {
        this.atmService = atmService;
    }

    public boolean insertCardAndLogin(String accountNumber, String pin) 
            throws AccountNotFoundException, InvalidPinException {
        Account account = atmService.authenticate(accountNumber, pin);
        this.currentSessionAccount = account;
        return true;
    }

    public boolean isAuthenticated() {
        return this.currentSessionAccount != null;
    }

    public double checkBalance() throws AccountNotFoundException {
        validateSession();
        return atmService.checkBalance(currentSessionAccount.getAccountNumber());
    }

    public Transaction deposit(double amount) throws AccountNotFoundException, InvalidAmountException {
        validateSession();
        return atmService.deposit(currentSessionAccount.getAccountNumber(), amount);
    }

    public Transaction withdraw(double amount) 
            throws AccountNotFoundException, InvalidAmountException, InsufficientBalanceException {
        validateSession();
        return atmService.withdraw(currentSessionAccount.getAccountNumber(), amount);
    }

    public List<Transaction> printMiniStatement() throws AccountNotFoundException {
        validateSession();
        return atmService.getMiniStatement(currentSessionAccount.getAccountNumber());
    }

    public void ejectCardAndLogout() {
        this.currentSessionAccount = null;
    }

    public Account getCurrentSessionAccount() {
        return currentSessionAccount;
    }

    private void validateSession() {
        if (!isAuthenticated()) {
            throw new IllegalStateException("No active ATM card/account session. Please log in first.");
        }
    }
}
