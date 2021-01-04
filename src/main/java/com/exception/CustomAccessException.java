package com.exception;

public class CustomAccessException extends IllegalAccessException {

    public CustomAccessException(String message){
        System.out.println(message);
    }
}
