package com.thedndpub.data.controllers.status;

import com.thedndpub.data.dto.status.StatusRecord;
import com.thedndpub.data.services.status.StatusService;
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
@RequestMapping("/api/statuses")
@Tag(name = "Statuses", description = "Canonical status thresholds, restrictions, modifiers, and ending rules")
public class StatusController {
    private final StatusService statusService;

    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping
    @Operation(summary = "Get all statuses")
    @ApiResponse(responseCode = "200", description = "Statuses in normalized dataset order")
    public List<StatusRecord> getAll() {
        return statusService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a status by its exact identifier")
    @ApiResponse(responseCode = "404", description = "No status has this identifier")
    public StatusRecord getById(@Parameter(description = "Exact status identifier") @PathVariable String id) {
        return statusService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status not found: " + id));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get statuses by exact name")
    @ApiResponse(responseCode = "200", description = "All statuses with this exact name")
    public List<StatusRecord> getByName(@Parameter(description = "Exact status name") @PathVariable String name) {
        return statusService.getByName(name);
    }
}
