package com.thedndpub.data.controllers;

import com.thedndpub.data.services.RewardNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = RewardsController.class)
public final class RewardsApiExceptionHandler {
    @ExceptionHandler(RewardNotFoundException.class)
    public ProblemDetail handleNotFound(RewardNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }
}
