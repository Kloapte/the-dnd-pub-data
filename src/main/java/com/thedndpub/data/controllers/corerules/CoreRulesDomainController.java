package com.thedndpub.data.controllers.corerules;

import com.thedndpub.data.services.corerules.CoreRulesDomainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

public abstract class CoreRulesDomainController {
    private final CoreRulesDomainService service;
    private final String domain;

    protected CoreRulesDomainController(CoreRulesDomainService service, String domain) {
        this.service = service;
        this.domain = domain;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List canonical records in the domain")
    @ApiResponse(responseCode = "200", description = "Complete canonical records in identity order")
    public byte[] findAll() {
        return service.findAllJson().orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Canonical domain resource is unavailable: " + domain));
    }

    @GetMapping(value = "/{source}/{slug}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Find a canonical record by source-qualified identity")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Complete canonical record"),
            @ApiResponse(responseCode = "404", description = "Canonical record not found")
    })
    public byte[] findBySourceAndSlug(
            @PathVariable("source") String source,
            @PathVariable("slug") String slug) {
        return service.findJsonBySourceAndSlug(source, slug).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Canonical Core Rules identity not found: " + domain + ":" + source + ":" + slug));
    }
}
