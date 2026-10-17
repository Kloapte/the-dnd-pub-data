package com.thedndpub.data.services;

public final class NameTableNotFoundException extends RuntimeException {
    public NameTableNotFoundException(String id) {
        super("Name table not found: " + id);
    }
}
