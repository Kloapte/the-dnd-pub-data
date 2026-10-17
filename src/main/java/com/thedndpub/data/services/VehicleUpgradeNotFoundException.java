package com.thedndpub.data.services;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public final class VehicleUpgradeNotFoundException extends RuntimeException {
    public VehicleUpgradeNotFoundException(String message) {
        super(message);
    }
}
