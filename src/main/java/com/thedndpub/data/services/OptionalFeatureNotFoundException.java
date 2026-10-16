package com.thedndpub.data.services;

public class OptionalFeatureNotFoundException extends RuntimeException {
    public OptionalFeatureNotFoundException(String message) {
        super(message);
    }
}
