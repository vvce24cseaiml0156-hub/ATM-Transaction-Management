package com.atm.model;

import com.atm.exception.InsufficientBalanceException;
import com.atm.exception.InvalidAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Account Model & Operation Unit Tests")
class AccountTest {

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account("ACC-001", "Alice Doe", "1234", 1000.00);
    }

    @Test
    @DisplayName("Verify account creation with valid details")
    void testAccountInitialization() {
        assertNotNull(account);
        assertEquals("ACC-001", account.getAccountNumber());
        assertEquals("Alice Doe", account.getAccountHolderName());
        assertEquals(1000.00, account.getBalance(), 0.001);
        assertTrue(account.validatePin("1234"));
        assertFalse(account.validatePin("9999"));
    }

    @Test
    @DisplayName("Balance enquiry returns correct initial balance")
    void testBalanceEnquiryReturnsCorrectBalance() {
        assertEquals(1000.00, account.getBalance(), 0.001);
    }

    @Test
    @DisplayName("Successful cash deposit updates balance correctly")
    void testSuccessfulDepositUpdatesBalance() throws InvalidAmountException {
        Transaction txn = account.deposit(500.00);

        assertNotNull(txn);
        assertEquals(TransactionType.DEPOSIT, txn.getType());
        assertEquals(500.00, txn.getAmount(), 0.001);
        assertEquals(1500.00, txn.getBalanceAfter(), 0.001);
        assertEquals(TransactionStatus.SUCCESS, txn.getStatus());
        assertEquals(1500.00, account.getBalance(), 0.001);
    }

    @Test
    @DisplayName("Successful cash withdrawal updates balance correctly")
    void testSuccessfulWithdrawalUpdatesBalance() throws InvalidAmountException, InsufficientBalanceException {
        Transaction txn = account.withdraw(400.00);

        assertNotNull(txn);
        assertEquals(TransactionType.WITHDRAWAL, txn.getType());
        assertEquals(400.00, txn.getAmount(), 0.001);
        assertEquals(600.00, txn.getBalanceAfter(), 0.001);
        assertEquals(TransactionStatus.SUCCESS, txn.getStatus());
        assertEquals(600.00, account.getBalance(), 0.001);
    }

    @Test
    @DisplayName("Withdrawal with insufficient balance is rejected")
    void testWithdrawalWithInsufficientBalanceRejected() {
        double initialBalance = account.getBalance();

        InsufficientBalanceException exception = assertThrows(
                InsufficientBalanceException.class,
                () -> account.withdraw(1500.00),
                "Should throw InsufficientBalanceException when withdrawal amount exceeds balance"
        );

        assertEquals(1500.00, exception.getRequestedAmount(), 0.001);
        assertEquals(1000.00, exception.getAvailableBalance(), 0.001);
        assertEquals(initialBalance, account.getBalance(), 0.001);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -10.0, -500.0})
    @DisplayName("Invalid deposit amounts are rejected")
    void testInvalidDepositAmountsRejected(double invalidAmount) {
        double initialBalance = account.getBalance();

        InvalidAmountException exception = assertThrows(
                InvalidAmountException.class,
                () -> account.deposit(invalidAmount),
                "Should throw InvalidAmountException for zero or negative deposit"
        );

        assertEquals(invalidAmount, exception.getInvalidAmount(), 0.001);
        assertEquals(initialBalance, account.getBalance(), 0.001);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -25.0, -100.0})
    @DisplayName("Invalid withdrawal amounts are rejected")
    void testInvalidWithdrawalAmountsRejected(double invalidAmount) {
        double initialBalance = account.getBalance();

        InvalidAmountException exception = assertThrows(
                InvalidAmountException.class,
                () -> account.withdraw(invalidAmount),
                "Should throw InvalidAmountException for zero or negative withdrawal"
        );

        assertEquals(invalidAmount, exception.getInvalidAmount(), 0.001);
        assertEquals(initialBalance, account.getBalance(), 0.001);
    }

    @Test
    @DisplayName("Failed transactions are recorded in audit history while balance is preserved")
    void testAuditHistoryOnFailure() {
        double initialBalance = account.getBalance();

        assertThrows(InsufficientBalanceException.class, () -> account.withdraw(5000.00));
        assertThrows(InvalidAmountException.class, () -> account.deposit(-50.00));

        assertEquals(initialBalance, account.getBalance(), 0.001);

        List<Transaction> history = account.getTransactionHistory();
        assertTrue(history.stream().anyMatch(t -> t.getStatus() == TransactionStatus.FAILED));
    }
}
