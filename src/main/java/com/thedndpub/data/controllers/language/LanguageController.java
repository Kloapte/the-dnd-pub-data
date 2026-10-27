package com.thedndpub.data.controllers.language;

import com.thedndpub.data.dto.language.LanguageRecord;
import com.thedndpub.data.services.language.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/languages")
@Tag(name = "Languages", description = "Canonical languages, dialects, and communication facts")
public class LanguageController {
    private final LanguageService languageService;

    public LanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    @GetMapping
    @Operation(summary = "Get all languages")
    @ApiResponse(responseCode = "200", description = "Languages in normalized dataset order")
    public List<LanguageRecord> getAll() {
        return languageService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a language by its exact identifier")
    @ApiResponse(responseCode = "404", description = "No language has this identifier")
    public LanguageRecord getById(@Parameter(description = "Exact language identifier") @PathVariable String id) {
        return languageService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Language not found: " + id));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get languages by exact name")
    @ApiResponse(responseCode = "200", description = "All languages with this exact name")
    public List<LanguageRecord> getByName(@Parameter(description = "Exact language name") @PathVariable String name) {
        return languageService.getByName(name);
    }
}
