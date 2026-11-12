package com.atm;

import com.atm.exception.AccountNotFoundException;
import com.atm.exception.InsufficientBalanceException;
import com.atm.exception.InvalidAmountException;
import com.atm.exception.InvalidPinException;
import com.atm.model.Account;
import com.atm.model.Transaction;
import com.atm.service.ATMService;
import com.atm.service.ATMServiceImpl;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("           JAVA-BASED ATM TRANSACTION MANAGEMENT APPLICATION                   ");
        System.out.println("===============================================================================");

        ATMService atmService = new ATMServiceImpl();
        ATM atm = new ATM(atmService);

        Account account1 = new Account("ACC-1001", "John Doe", "1234", 1500.00);
        Account account2 = new Account("ACC-1002", "Alice Smith", "4321", 5000.00);
        atmService.registerAccount(account1);
        atmService.registerAccount(account2);

        System.out.println("[INFO] Accounts successfully registered into the core banking system.");
        System.out.printf("       Account 1: %s (%s) | Starting Balance: $%.2f%n",
                account1.getAccountNumber(), account1.getAccountHolderName(), account1.getBalance());
        System.out.printf("       Account 2: %s (%s) | Starting Balance: $%.2f%n",
                account2.getAccountNumber(), account2.getAccountHolderName(), account2.getBalance());
        System.out.println();

        String targetAcc = "ACC-1001";
        String targetPin = "1234";

        try {
            System.out.println(">>> STEP 1: Authenticating card for " + targetAcc + "...");
            atm.insertCardAndLogin(targetAcc, targetPin);
            System.out.println("[SUCCESS] Welcome, " + atm.getCurrentSessionAccount().getAccountHolderName() + "!");

            System.out.println("\n>>> STEP 2: Balance Enquiry...");
            double currentBalance = atm.checkBalance();
            System.out.printf("[SUCCESS] Current Available Balance: $%.2f%n", currentBalance);

            double depositAmount = 500.00;
            System.out.printf("\n>>> STEP 3: Depositing Cash: $%.2f...%n", depositAmount);
            Transaction depositTxn = atm.deposit(depositAmount);
            System.out.println("[SUCCESS] " + depositTxn.getDescription());
            System.out.printf("          Updated Balance after Deposit: $%.2f%n", atm.checkBalance());

            double withdrawAmount = 300.00;
            System.out.printf("\n>>> STEP 4: Withdrawing Cash: $%.2f...%n", withdrawAmount);
            Transaction withdrawTxn = atm.withdraw(withdrawAmount);
            System.out.println("[SUCCESS] " + withdrawTxn.getDescription());
            System.out.printf("          Updated Balance after Withdrawal: $%.2f%n", atm.checkBalance());

            double excessiveAmount = 50000.00;
            System.out.printf("\n>>> STEP 5: Testing Insufficient Balance Withdrawal: $%.2f...%n", excessiveAmount);
            try {
                atm.withdraw(excessiveAmount);
            } catch (InsufficientBalanceException e) {
                System.out.println("[EXPECTED ERROR CAUGHT] " + e.getMessage());
                System.out.printf("                        Balance preserved unchanged at: $%.2f%n", atm.checkBalance());
            }

            double negativeDeposit = -100.00;
            System.out.printf("\n>>> STEP 6: Testing Invalid Deposit Amount: $%.2f...%n", negativeDeposit);
            try {
                atm.deposit(negativeDeposit);
            } catch (InvalidAmountException e) {
                System.out.println("[EXPECTED ERROR CAUGHT] " + e.getMessage());
                System.out.printf("                        Balance preserved unchanged at: $%.2f%n", atm.checkBalance());
            }

            double zeroWithdraw = 0.00;
            System.out.printf("\n>>> STEP 7: Testing Invalid Withdrawal Amount: $%.2f...%n", zeroWithdraw);
            try {
                atm.withdraw(zeroWithdraw);
            } catch (InvalidAmountException e) {
                System.out.println("[EXPECTED ERROR CAUGHT] " + e.getMessage());
                System.out.printf("                        Balance preserved unchanged at: $%.2f%n", atm.checkBalance());
            }

            System.out.println("\n>>> STEP 8: Fetching Mini-Statement & Audit Trail...");
            List<Transaction> history = atm.printMiniStatement();
            System.out.println("-------------------------------------------------------------------------------");
            for (Transaction txn : history) {
                System.out.println(txn);
            }
            System.out.println("-------------------------------------------------------------------------------");

            atm.ejectCardAndLogout();
            System.out.println("\n[INFO] Card ejected. Session ended securely.");

        } catch (AccountNotFoundException | InvalidPinException | InvalidAmountException | InsufficientBalanceException e) {
            System.err.println("[FATAL ERROR] Unexpected exception during execution: " + e.getMessage());
        }

        System.out.println("\n===============================================================================");
        System.out.println("               ALL ATM OPERATIONS COMPLETED SUCCESSFULLY!                      ");
        System.out.println("===============================================================================");
    }
}
