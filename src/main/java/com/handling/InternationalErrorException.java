package com.handling;

public class InternationalErrorException extends RuntimeException {
    public InternationalErrorException(String message) {
        super(message);
    }

    public InternationalErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
