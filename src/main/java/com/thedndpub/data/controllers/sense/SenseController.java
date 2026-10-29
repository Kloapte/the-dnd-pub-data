package com.thedndpub.data.controllers.sense;

import com.thedndpub.data.dto.sense.SenseRecord;
import com.thedndpub.data.services.sense.SenseService;
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
@RequestMapping("/api/senses")
@Tag(name = "Senses", description = "Canonical sense capabilities and interactions")
public class SenseController {
    private final SenseService senseService;

    public SenseController(SenseService senseService) {
        this.senseService = senseService;
    }

    @GetMapping
    @Operation(summary = "Get all senses")
    @ApiResponse(responseCode = "200", description = "Senses in normalized dataset order")
    public List<SenseRecord> getAll() {
        return senseService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a sense by its exact identifier")
    @ApiResponse(responseCode = "404", description = "No sense has this identifier")
    public SenseRecord getById(@Parameter(description = "Exact sense identifier") @PathVariable String id) {
        return senseService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sense not found: " + id));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get senses by exact name")
    @ApiResponse(responseCode = "200", description = "All senses with this exact name")
    public List<SenseRecord> getByName(@Parameter(description = "Exact sense name") @PathVariable String name) {
        return senseService.getByName(name);
    }
}
