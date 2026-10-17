package com.thedndpub.data.services;

public final class BoonNotFoundException extends RuntimeException {
    public BoonNotFoundException(String id) {
        super("No boon definition found for ID '" + id + "'");
    }
}
