package com.atm.service;

import com.atm.exception.AccountNotFoundException;
import com.atm.exception.InsufficientBalanceException;
import com.atm.exception.InvalidAmountException;
import com.atm.exception.InvalidPinException;
import com.atm.model.Account;
import com.atm.model.Transaction;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ATMServiceImpl implements ATMService {
    private final Map<String, Account> accountRepository;

    public ATMServiceImpl() {
        this.accountRepository = new ConcurrentHashMap<>();
    }

    @Override
    public void registerAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account to register cannot be null.");
        }
        accountRepository.put(account.getAccountNumber(), account);
    }

    @Override
    public Account authenticate(String accountNumber, String pin) throws AccountNotFoundException, InvalidPinException {
        Account account = getAccountOrThrow(accountNumber);
        if (!account.validatePin(pin)) {
            throw new InvalidPinException();
        }
        return account;
    }

    @Override
    public double checkBalance(String accountNumber) throws AccountNotFoundException {
        Account account = getAccountOrThrow(accountNumber);
        account.recordBalanceEnquiry();
        return account.getBalance();
    }

    @Override
    public Transaction deposit(String accountNumber, double amount) 
            throws AccountNotFoundException, InvalidAmountException {
        Account account = getAccountOrThrow(accountNumber);
        return account.deposit(amount);
    }

    @Override
    public Transaction withdraw(String accountNumber, double amount) 
            throws AccountNotFoundException, InvalidAmountException, InsufficientBalanceException {
        Account account = getAccountOrThrow(accountNumber);
        return account.withdraw(amount);
    }

    @Override
    public List<Transaction> getMiniStatement(String accountNumber) throws AccountNotFoundException {
        Account account = getAccountOrThrow(accountNumber);
        return account.getTransactionHistory();
    }

    private Account getAccountOrThrow(String accountNumber) throws AccountNotFoundException {
        Account account = accountRepository.get(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException(accountNumber);
        }
        return account;
    }
}
