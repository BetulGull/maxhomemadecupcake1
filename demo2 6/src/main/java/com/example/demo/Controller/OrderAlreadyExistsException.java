package com.example.demo.Controller;

public class OrderAlreadyExistsException extends Exception{
    public OrderAlreadyExistsException(String message) {
        super(message);
    }


}
