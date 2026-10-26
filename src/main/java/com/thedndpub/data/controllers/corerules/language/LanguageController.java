package com.thedndpub.data.controllers.corerules.language;

import com.thedndpub.data.dto.corerules.language.LanguageRecord;
import com.thedndpub.data.services.corerules.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/core-rules/language")
@Tag(name = "Language", description = "Read-only canonical Language catalog")
public class LanguageController {
    private final LanguageService service;

    public LanguageController(LanguageService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List canonical Languages")
    @ApiResponse(responseCode = "200", description = "Typed Language records in identity order")
    public List<LanguageRecord> findAll() {
        return service.findAllLanguages();
    }

    @GetMapping("/{source}/{slug}")
    @Operation(summary = "Find a Language by source-qualified identity")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Canonical Language record"),
            @ApiResponse(responseCode = "404", description = "Language not found")
    })
    public LanguageRecord findByIdentity(
            @Parameter(description = "Lowercase canonical source component") @PathVariable("source") String source,
            @Parameter(description = "Canonical Language name slug") @PathVariable("slug") String slug) {
        return service.findLanguage(source, slug).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Canonical Language identity not found: language:" + source + ":" + slug));
    }
}
