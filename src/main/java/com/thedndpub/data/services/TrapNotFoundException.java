package com.thedndpub.data.services;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public final class TrapNotFoundException extends RuntimeException {
    public TrapNotFoundException(String id) {
        super("Trap not found: " + id);
    }
}
