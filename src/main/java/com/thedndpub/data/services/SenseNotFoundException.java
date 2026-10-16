package com.thedndpub.data.services;

public class SenseNotFoundException extends RuntimeException {
    public SenseNotFoundException(String message) {
        super(message);
    }
}
