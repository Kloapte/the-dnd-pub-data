package com.thedndpub.data.services;

public class PublicationNotFoundException extends RuntimeException {
    public PublicationNotFoundException(String message) {
        super(message);
    }
}
