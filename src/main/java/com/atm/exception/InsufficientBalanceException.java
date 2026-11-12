package com.atm.exception;

public class InsufficientBalanceException extends ATMException {
    private final double requestedAmount;
    private final double availableBalance;

    public InsufficientBalanceException(double requestedAmount, double availableBalance) {
        super(String.format("Insufficient balance: Requested $%.2f, but available balance is only $%.2f.", 
                requestedAmount, availableBalance));
        this.requestedAmount = requestedAmount;
        this.availableBalance = availableBalance;
    }

    public double getRequestedAmount() {
        return requestedAmount;
    }

    public double getAvailableBalance() {
        return availableBalance;
    }
}
