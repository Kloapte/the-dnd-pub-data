package com.thedndpub.data.services;

public final class CultNotFoundException extends RuntimeException {
    public CultNotFoundException(String id) {
        super("No cult definition found for ID '" + id + "'");
    }
}
