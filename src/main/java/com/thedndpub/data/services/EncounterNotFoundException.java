package com.thedndpub.data.services;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public final class EncounterNotFoundException extends RuntimeException {
    public EncounterNotFoundException(String id) {
        super("Encounter not found: " + id);
    }
}
