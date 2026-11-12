package com.atm;

import com.atm.exception.AccountNotFoundException;
import com.atm.exception.InsufficientBalanceException;
import com.atm.exception.InvalidAmountException;
import com.atm.exception.InvalidPinException;
import com.atm.model.Account;
import com.atm.model.Transaction;
import com.atm.model.TransactionStatus;
import com.atm.model.TransactionType;
import com.atm.service.ATMService;
import com.atm.service.ATMServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ATM Controller End-to-End Terminal Tests")
class ATMTest {

    private ATM atm;
    private ATMService atmService;

    @BeforeEach
    void setUp() {
        atmService = new ATMServiceImpl();
        Account account = new Account("ACC-3001", "Charlie Brown", "5555", 3000.00);
        atmService.registerAccount(account);
        atm = new ATM(atmService);
    }

    @Test
    @DisplayName("Operations before login throw IllegalStateException")
    void testOperationsWithoutSession() {
        assertFalse(atm.isAuthenticated());
        assertThrows(IllegalStateException.class, () -> atm.checkBalance());
        assertThrows(IllegalStateException.class, () -> atm.deposit(100.00));
        assertThrows(IllegalStateException.class, () -> atm.withdraw(100.00));
        assertThrows(IllegalStateException.class, () -> atm.printMiniStatement());
    }

    @Test
    @DisplayName("Successful login, balance check, deposit, and withdrawal lifecycle")
    void testCompleteATMWorkflow() throws AccountNotFoundException, InvalidPinException,
            InvalidAmountException, InsufficientBalanceException {
        assertTrue(atm.insertCardAndLogin("ACC-3001", "5555"));
        assertTrue(atm.isAuthenticated());
        assertEquals("Charlie Brown", atm.getCurrentSessionAccount().getAccountHolderName());

        assertEquals(3000.00, atm.checkBalance(), 0.001);

        Transaction depositTxn = atm.deposit(1000.00);
        assertNotNull(depositTxn);
        assertEquals(TransactionType.DEPOSIT, depositTxn.getType());
        assertEquals(TransactionStatus.SUCCESS, depositTxn.getStatus());
        assertEquals(4000.00, atm.checkBalance(), 0.001);

        Transaction withdrawTxn = atm.withdraw(1500.00);
        assertNotNull(withdrawTxn);
        assertEquals(TransactionType.WITHDRAWAL, withdrawTxn.getType());
        assertEquals(TransactionStatus.SUCCESS, withdrawTxn.getStatus());
        assertEquals(2500.00, atm.checkBalance(), 0.001);

        assertThrows(InsufficientBalanceException.class, () -> atm.withdraw(50000.00));
        assertEquals(2500.00, atm.checkBalance(), 0.001);

        assertThrows(InvalidAmountException.class, () -> atm.deposit(-100.00));
        assertThrows(InvalidAmountException.class, () -> atm.withdraw(0.00));
        assertEquals(2500.00, atm.checkBalance(), 0.001);

        List<Transaction> statement = atm.printMiniStatement();
        assertFalse(statement.isEmpty());

        atm.ejectCardAndLogout();
        assertFalse(atm.isAuthenticated());
        assertThrows(IllegalStateException.class, () -> atm.checkBalance());
    }
}
