package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.source.Source;
import com.thedndpub.data.services.SourceRegistry;
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
@RequestMapping("/api/sources")
@Tag(name = "Sources", description = "Read-only registry of canonical source identities")
public class SourceController {
    private final SourceRegistry sourceRegistry;

    public SourceController(SourceRegistry sourceRegistry) {
        this.sourceRegistry = sourceRegistry;
    }

    @GetMapping
    @Operation(summary = "List all canonical sources")
    @ApiResponse(responseCode = "200", description = "Sources in canonical dataset order")
    public List<Source> findAll() {
        return sourceRegistry.findAll();
    }

    @GetMapping("/{code}")
    @Operation(summary = "Find a source by canonical code or declared alias")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The canonical Source entity"),
            @ApiResponse(responseCode = "404", description = "No Source code or declared alias matches the value")
    })
    public Source findByCodeOrAlias(
            @Parameter(description = "Exact canonical source code or explicitly declared code alias")
            @PathVariable String code) {
        return sourceRegistry.findByCodeOrAlias(code)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Source code or alias not found: " + code));
    }
}
