package com.atm.model;

import com.atm.exception.InsufficientBalanceException;
import com.atm.exception.InvalidAmountException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Account {
    private final String accountNumber;
    private final String accountHolderName;
    private final String pin;
    private double balance;
    private final List<Transaction> transactionHistory;

    public Account(String accountNumber, String accountHolderName, String pin, double initialBalance) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be null or empty.");
        }
        if (accountHolderName == null || accountHolderName.trim().isEmpty()) {
            throw new IllegalArgumentException("Account holder name cannot be null or empty.");
        }
        if (pin == null || pin.length() < 4) {
            throw new IllegalArgumentException("PIN must be at least 4 digits.");
        }
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }

        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.pin = pin;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();

        if (initialBalance > 0) {
            this.transactionHistory.add(new Transaction(
                    TransactionType.DEPOSIT,
                    initialBalance,
                    initialBalance,
                    TransactionStatus.SUCCESS,
                    "Initial Opening Deposit"
            ));
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public boolean validatePin(String inputPin) {
        return this.pin.equals(inputPin);
    }

    public synchronized double getBalance() {
        return balance;
    }

    public synchronized Transaction deposit(double amount) throws InvalidAmountException {
        if (Double.isNaN(amount) || Double.isInfinite(amount) || amount <= 0) {
            recordFailedTransaction(TransactionType.DEPOSIT, amount, "Deposit amount must be strictly greater than 0.");
            throw new InvalidAmountException("Deposit amount must be strictly greater than 0.", amount);
        }

        this.balance += amount;
        Transaction txn = new Transaction(
                TransactionType.DEPOSIT,
                amount,
                this.balance,
                TransactionStatus.SUCCESS,
                "Cash Deposit Successful"
        );
        this.transactionHistory.add(txn);
        return txn;
    }

    public synchronized Transaction withdraw(double amount) throws InvalidAmountException, InsufficientBalanceException {
        if (Double.isNaN(amount) || Double.isInfinite(amount) || amount <= 0) {
            recordFailedTransaction(TransactionType.WITHDRAWAL, amount, "Withdrawal amount must be strictly greater than 0.");
            throw new InvalidAmountException("Withdrawal amount must be strictly greater than 0.", amount);
        }

        if (amount > this.balance) {
            recordFailedTransaction(TransactionType.WITHDRAWAL, amount,
                    String.format("Insufficient funds: Required $%.2f, Available $%.2f", amount, this.balance));
            throw new InsufficientBalanceException(amount, this.balance);
        }

        this.balance -= amount;
        Transaction txn = new Transaction(
                TransactionType.WITHDRAWAL,
                amount,
                this.balance,
                TransactionStatus.SUCCESS,
                "Cash Withdrawal Successful"
        );
        this.transactionHistory.add(txn);
        return txn;
    }

    public synchronized Transaction recordBalanceEnquiry() {
        Transaction txn = new Transaction(
                TransactionType.BALANCE_ENQUIRY,
                0.0,
                this.balance,
                TransactionStatus.SUCCESS,
                "Balance Enquiry Performed"
        );
        this.transactionHistory.add(txn);
        return txn;
    }

    public synchronized void recordFailedTransaction(TransactionType type, double amount, String reason) {
        Transaction txn = new Transaction(
                type,
                amount,
                this.balance,
                TransactionStatus.FAILED,
                reason
        );
        this.transactionHistory.add(txn);
    }

    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(new ArrayList<>(transactionHistory));
    }
}
