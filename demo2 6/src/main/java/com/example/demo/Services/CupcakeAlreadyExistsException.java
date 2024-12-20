package com.example.demo.Services;

public class CupcakeAlreadyExistsException extends Exception {
    public CupcakeAlreadyExistsException(String message) {
        super(message);
    }
}
