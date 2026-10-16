package com.thedndpub.data.services;

public final class BackgroundNotFoundException extends RuntimeException {
    public BackgroundNotFoundException(String message) {
        super(message);
    }
}
