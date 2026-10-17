package com.thedndpub.data.controllers;

import com.thedndpub.data.services.CharacterCreationOptionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = CharacterCreationOptionsController.class)
public final class CharacterCreationOptionApiExceptionHandler {
    @ExceptionHandler(CharacterCreationOptionNotFoundException.class)
    public ProblemDetail handleNotFound(CharacterCreationOptionNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }
}
