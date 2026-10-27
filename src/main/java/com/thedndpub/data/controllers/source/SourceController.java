package com.thedndpub.data.controllers.source;

import com.thedndpub.data.dto.source.Source;
import com.thedndpub.data.services.source.SourceService;
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
@RequestMapping("/api/sources")
@Tag(name = "Sources", description = "Read-only registry of canonical source identities")
public class SourceController {
    private final SourceService sourceService;

    public SourceController(SourceService sourceService) {
        this.sourceService = sourceService;
    }

    @GetMapping
    @Operation(summary = "Get all sources")
    @ApiResponse(responseCode = "200", description = "Sources in normalized dataset order")
    public List<Source> getAll() {
        return sourceService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a source by its exact source code")
    @ApiResponse(responseCode = "404", description = "No source has this code")
    public Source getById(@Parameter(description = "Exact source code") @PathVariable String id) {
        return sourceService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Source not found: " + id));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get a source by publication name")
    @ApiResponse(responseCode = "404", description = "No source publication has this name")
    public Source getByName(@Parameter(description = "Exact publication name") @PathVariable String name) {
        return sourceService.getByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Source not found: " + name));
    }
}
