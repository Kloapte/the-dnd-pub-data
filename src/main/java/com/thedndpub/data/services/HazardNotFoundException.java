package com.thedndpub.data.services;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public final class HazardNotFoundException extends RuntimeException {
    public HazardNotFoundException(String id) {
        super("Hazard not found: " + id);
    }
}
