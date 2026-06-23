package com.api_store.exception;

public class BadRequestException extends RuntimeException{
    public BadRequestException(){
        super("Bad Request Raised");
    }
}
