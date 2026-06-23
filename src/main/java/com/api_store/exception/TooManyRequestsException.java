package com.api_store.exception;

public class TooManyRequestsException extends RuntimeException {

    public TooManyRequestsException(String message) {

        super(message);
    }
}
