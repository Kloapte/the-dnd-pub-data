package com.thedndpub.data.services;

public final class AdventureNotFoundException extends RuntimeException {
    public AdventureNotFoundException(String id) {
        super("Adventure not found: " + id);
    }
}
