package com.thedndpub.data.controllers;

import com.thedndpub.data.services.MonsterNotFoundException;
import com.thedndpub.data.services.LanguageNotFoundException;
import com.thedndpub.data.services.PublicationNotFoundException;
import com.thedndpub.data.services.SkillNotFoundException;
import com.thedndpub.data.services.SenseNotFoundException;
import com.thedndpub.data.services.ConditionNotFoundException;
import com.thedndpub.data.services.DiseaseNotFoundException;
import com.thedndpub.data.services.ActionNotFoundException;
import com.thedndpub.data.services.FeatNotFoundException;
import com.thedndpub.data.services.OptionalFeatureNotFoundException;
import com.thedndpub.data.services.ItemNotFoundException;
import com.thedndpub.data.services.MagicVariantNotFoundException;
import com.thedndpub.data.services.BackgroundNotFoundException;
import com.thedndpub.data.services.RaceNotFoundException;
import com.thedndpub.data.services.SpellNotFoundException;
import com.thedndpub.data.services.AdventureNotFoundException;
import com.thedndpub.data.services.NameTableNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class MonsterApiExceptionHandler {
    @ExceptionHandler({
            MonsterNotFoundException.class,
            LanguageNotFoundException.class,
            PublicationNotFoundException.class,
            SkillNotFoundException.class,
            SenseNotFoundException.class,
            ConditionNotFoundException.class,
            DiseaseNotFoundException.class,
            ActionNotFoundException.class,
            FeatNotFoundException.class,
            OptionalFeatureNotFoundException.class,
            ItemNotFoundException.class,
            MagicVariantNotFoundException.class,
            BackgroundNotFoundException.class,
            RaceNotFoundException.class,
            SpellNotFoundException.class,
            AdventureNotFoundException.class,
            NameTableNotFoundException.class
    })
    public ProblemDetail handleNotFound(RuntimeException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleInvalidArgument(IllegalArgumentException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail handleMissingParameter(MissingServletRequestParameterException exception) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Required query parameter '" + exception.getParameterName() + "' is missing");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Invalid value for parameter '" + exception.getName() + "'");
    }
}
