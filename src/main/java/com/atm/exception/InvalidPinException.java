package com.atm.exception;

public class InvalidPinException extends ATMException {
    public InvalidPinException() {
        super("Invalid PIN entered. Authentication failed.");
    }
}
