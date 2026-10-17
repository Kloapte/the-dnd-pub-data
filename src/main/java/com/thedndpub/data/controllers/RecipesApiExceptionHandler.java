package com.thedndpub.data.controllers;

import com.thedndpub.data.services.RecipeNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = RecipesController.class)
public final class RecipesApiExceptionHandler {
    @ExceptionHandler(RecipeNotFoundException.class)
    public ProblemDetail handleNotFound(RecipeNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }
}
