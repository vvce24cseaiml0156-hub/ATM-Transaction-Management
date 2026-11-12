package com.atm.service;

import com.atm.exception.AccountNotFoundException;
import com.atm.exception.InsufficientBalanceException;
import com.atm.exception.InvalidAmountException;
import com.atm.exception.InvalidPinException;
import com.atm.model.Account;
import com.atm.model.Transaction;

import java.util.List;

public interface ATMService {
    void registerAccount(Account account);
    Account authenticate(String accountNumber, String pin) throws AccountNotFoundException, InvalidPinException;
    double checkBalance(String accountNumber) throws AccountNotFoundException;
    Transaction deposit(String accountNumber, double amount) throws AccountNotFoundException, InvalidAmountException;
    Transaction withdraw(String accountNumber, double amount) throws AccountNotFoundException, InvalidAmountException, InsufficientBalanceException;
    List<Transaction> getMiniStatement(String accountNumber) throws AccountNotFoundException;
}
