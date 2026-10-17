package com.thedndpub.data.controllers;

import com.thedndpub.data.services.BoonNotFoundException;
import com.thedndpub.data.services.CultNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {CultsController.class, BoonsController.class})
public final class CultsAndBoonsApiExceptionHandler {
    @ExceptionHandler({CultNotFoundException.class, BoonNotFoundException.class})
    public ProblemDetail handleNotFound(RuntimeException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }
}
