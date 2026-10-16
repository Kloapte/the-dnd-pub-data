package com.thedndpub.data.services;

public final class DiseaseNotFoundException extends RuntimeException {
    public DiseaseNotFoundException(String message) {
        super(message);
    }
}
