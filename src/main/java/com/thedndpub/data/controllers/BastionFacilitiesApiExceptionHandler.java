package com.thedndpub.data.controllers;

import com.thedndpub.data.services.BastionFacilityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = BastionFacilitiesController.class)
public final class BastionFacilitiesApiExceptionHandler {
    @ExceptionHandler(BastionFacilityNotFoundException.class)
    public ProblemDetail handleNotFound(BastionFacilityNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }
}
