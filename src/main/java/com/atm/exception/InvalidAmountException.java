package com.atm.exception;

public class InvalidAmountException extends ATMException {
    private final double invalidAmount;

    public InvalidAmountException(String message, double invalidAmount) {
        super(message);
        this.invalidAmount = invalidAmount;
    }

    public double getInvalidAmount() {
        return invalidAmount;
    }
}
