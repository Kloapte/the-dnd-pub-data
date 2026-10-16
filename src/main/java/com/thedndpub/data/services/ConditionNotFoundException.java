package com.thedndpub.data.services;

public final class ConditionNotFoundException extends RuntimeException {
    public ConditionNotFoundException(String message) {
        super(message);
    }
}
