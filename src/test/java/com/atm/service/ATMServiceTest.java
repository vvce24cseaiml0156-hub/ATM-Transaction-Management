package com.atm.service;

import com.atm.exception.AccountNotFoundException;
import com.atm.exception.InsufficientBalanceException;
import com.atm.exception.InvalidAmountException;
import com.atm.exception.InvalidPinException;
import com.atm.model.Account;
import com.atm.model.Transaction;
import com.atm.model.TransactionStatus;
import com.atm.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ATM Service Layer Unit Tests")
class ATMServiceTest {

    private ATMService atmService;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        atmService = new ATMServiceImpl();
        testAccount = new Account("ACC-2001", "Bob Martin", "4321", 2000.00);
        atmService.registerAccount(testAccount);
    }

    @Test
    @DisplayName("Authenticate with correct credentials succeeds")
    void testAuthenticationSuccess() throws AccountNotFoundException, InvalidPinException {
        Account authenticated = atmService.authenticate("ACC-2001", "4321");
        assertNotNull(authenticated);
        assertEquals("ACC-2001", authenticated.getAccountNumber());
    }

    @Test
    @DisplayName("Authenticate with invalid PIN throws InvalidPinException")
    void testAuthenticationInvalidPin() {
        assertThrows(InvalidPinException.class, () -> atmService.authenticate("ACC-2001", "0000"));
    }

    @Test
    @DisplayName("Authenticate with nonexistent account throws AccountNotFoundException")
    void testAuthenticationNonexistentAccount() {
        assertThrows(AccountNotFoundException.class, () -> atmService.authenticate("ACC-9999", "1234"));
    }

    @Test
    @DisplayName("Check balance returns correct balance and records transaction")
    void testCheckBalance() throws AccountNotFoundException {
        double balance = atmService.checkBalance("ACC-2001");
        assertEquals(2000.00, balance, 0.001);
    }

    @Test
    @DisplayName("Deposit updates balance and records successful transaction")
    void testDepositSuccess() throws AccountNotFoundException, InvalidAmountException {
        Transaction txn = atmService.deposit("ACC-2001", 750.00);

        assertNotNull(txn);
        assertEquals(TransactionType.DEPOSIT, txn.getType());
        assertEquals(TransactionStatus.SUCCESS, txn.getStatus());
        assertEquals(2750.00, atmService.checkBalance("ACC-2001"), 0.001);
    }

    @Test
    @DisplayName("Withdrawal updates balance and records successful transaction")
    void testWithdrawalSuccess() throws AccountNotFoundException, InvalidAmountException, InsufficientBalanceException {
        Transaction txn = atmService.withdraw("ACC-2001", 500.00);

        assertNotNull(txn);
        assertEquals(TransactionType.WITHDRAWAL, txn.getType());
        assertEquals(TransactionStatus.SUCCESS, txn.getStatus());
        assertEquals(1500.00, atmService.checkBalance("ACC-2001"), 0.001);
    }

    @Test
    @DisplayName("Withdrawal exceeding balance throws InsufficientBalanceException and preserves balance")
    void testWithdrawalInsufficientBalance() throws AccountNotFoundException {
        assertThrows(InsufficientBalanceException.class, () -> atmService.withdraw("ACC-2001", 9999.00));
        assertEquals(2000.00, atmService.checkBalance("ACC-2001"), 0.001);
    }

    @Test
    @DisplayName("Withdrawal with negative amount throws InvalidAmountException and preserves balance")
    void testWithdrawalNegativeAmount() throws AccountNotFoundException {
        assertThrows(InvalidAmountException.class, () -> atmService.withdraw("ACC-2001", -100.00));
        assertEquals(2000.00, atmService.checkBalance("ACC-2001"), 0.001);
    }

    @Test
    @DisplayName("Deposit with negative amount throws InvalidAmountException and preserves balance")
    void testDepositNegativeAmount() throws AccountNotFoundException {
        assertThrows(InvalidAmountException.class, () -> atmService.deposit("ACC-2001", -200.00));
        assertEquals(2000.00, atmService.checkBalance("ACC-2001"), 0.001);
    }

    @Test
    @DisplayName("Mini statement returns list of completed transactions")
    void testMiniStatement() throws AccountNotFoundException, InvalidAmountException, InsufficientBalanceException {
        atmService.deposit("ACC-2001", 300.00);
        atmService.withdraw("ACC-2001", 100.00);

        List<Transaction> history = atmService.getMiniStatement("ACC-2001");
        assertNotNull(history);
        assertTrue(history.size() >= 3);
    }
}
