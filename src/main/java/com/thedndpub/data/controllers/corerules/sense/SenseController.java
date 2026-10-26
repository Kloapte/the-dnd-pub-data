package com.thedndpub.data.controllers.corerules.sense;

import com.thedndpub.data.dto.corerules.sense.SenseRecord;
import com.thedndpub.data.services.corerules.SenseService;
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
@RequestMapping("/api/core-rules/sense")
@Tag(name = "Sense", description = "Read-only canonical Sense catalog")
public class SenseController {
    private final SenseService service;

    public SenseController(SenseService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List canonical Senses")
    @ApiResponse(responseCode = "200", description = "Typed Sense records in identity order")
    public List<SenseRecord> findAll() {
        return service.findAllSenses();
    }

    @GetMapping("/{source}/{slug}")
    @Operation(summary = "Find a Sense by source-qualified identity")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Canonical Sense record"),
            @ApiResponse(responseCode = "404", description = "Sense not found")
    })
    public SenseRecord findByIdentity(
            @Parameter(description = "Lowercase canonical source component") @PathVariable("source") String source,
            @Parameter(description = "Canonical Sense name slug") @PathVariable("slug") String slug) {
        return service.findSense(source, slug).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Canonical Sense identity not found: sense:" + source + ":" + slug));
    }
}
