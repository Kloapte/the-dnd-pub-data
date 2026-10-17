package com.thedndpub.data.services;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public final class TreasureTableNotFoundException extends RuntimeException {
    public TreasureTableNotFoundException(String id) {
        super("Treasure table not found: " + id);
    }
}
