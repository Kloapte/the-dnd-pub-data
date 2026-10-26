package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.corerules.CanonicalReference;
import com.thedndpub.data.dto.corerules.ReferenceResolution;
import com.thedndpub.data.services.CoreRulesCatalog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/core-rules/references")
@Tag(name = "Core Rules References", description = "Source-aware canonical reference resolution")
public class CoreRulesReferenceController {
    private final CoreRulesCatalog catalog;

    public CoreRulesReferenceController(CoreRulesCatalog catalog) {
        this.catalog = catalog;
    }

    @GetMapping("/resolve")
    @Operation(summary = "Resolve a typed reference against canonical and pinned source identities")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reference resolution state"),
            @ApiResponse(responseCode = "400", description = "Invalid typed reference")
    })
    public ReferenceResolution resolveReference(
            @Parameter(description = "Reference target domain") @RequestParam String targetType,
            @Parameter(description = "Source-qualified canonical identity") @RequestParam String id,
            @Parameter(description = "Optional relationship label") @RequestParam(required = false) String relationship,
            @Parameter(description = "Optional mechanical operation label") @RequestParam(required = false) String operation) {
        try {
            return catalog.resolve(new CanonicalReference(targetType, id, relationship, operation));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }
    }
}
