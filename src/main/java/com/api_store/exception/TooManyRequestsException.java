package com.api_store.exception;

public class TooManyRequestsException extends RuntimeException {

    public TooManyRequestsException() {

        super("Rate limit exceed");
    }
}
