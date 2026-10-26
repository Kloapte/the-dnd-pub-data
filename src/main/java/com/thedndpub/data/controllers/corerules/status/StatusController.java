package com.thedndpub.data.controllers.corerules.status;

import com.thedndpub.data.dto.corerules.status.StatusRecord;
import com.thedndpub.data.services.corerules.StatusService;
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
@RequestMapping("/api/core-rules/status")
@Tag(name = "Status", description = "Read-only canonical Status catalog")
public class StatusController {
    private final StatusService service;

    public StatusController(StatusService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List canonical Status records")
    @ApiResponse(responseCode = "200", description = "Typed Status records in identity order")
    public List<StatusRecord> findAll() {
        return service.findAllStatuses();
    }

    @GetMapping("/{source}/{slug}")
    @Operation(summary = "Find a Status by source-qualified identity")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Canonical Status record"),
            @ApiResponse(responseCode = "404", description = "Status not found")
    })
    public StatusRecord findByIdentity(
            @Parameter(description = "Lowercase canonical source component") @PathVariable("source") String source,
            @Parameter(description = "Canonical Status name slug") @PathVariable("slug") String slug) {
        return service.findStatus(source, slug).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Canonical Status identity not found: status:" + source + ":" + slug));
    }
}
