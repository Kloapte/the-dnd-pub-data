package com.thedndpub.data.services;

public final class DeityNotFoundException extends RuntimeException {
    public DeityNotFoundException(String message) {
        super(message);
    }
}
