package com.thedndpub.data.services;

public final class SpellNotFoundException extends RuntimeException {
    public SpellNotFoundException(String message) {
        super(message);
    }
}
