package com.thedndpub.data.controllers.condition;

import com.thedndpub.data.dto.condition.ConditionRecord;
import com.thedndpub.data.services.condition.ConditionService;
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
@RequestMapping("/api/conditions")
@Tag(name = "Conditions", description = "Canonical condition mechanics")
public class ConditionController {
    private final ConditionService conditionService;

    public ConditionController(ConditionService conditionService) {
        this.conditionService = conditionService;
    }

    @GetMapping
    @Operation(summary = "Get all conditions")
    @ApiResponse(responseCode = "200", description = "Conditions in normalized dataset order")
    public List<ConditionRecord> getAll() {
        return conditionService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a condition by its exact identifier")
    @ApiResponse(responseCode = "404", description = "No condition has this identifier")
    public ConditionRecord getById(@Parameter(description = "Exact condition identifier") @PathVariable String id) {
        return conditionService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Condition not found: " + id));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get conditions by exact name")
    @ApiResponse(responseCode = "200", description = "All conditions with this exact name")
    public List<ConditionRecord> getByName(
            @Parameter(description = "Exact condition name") @PathVariable String name) {
        return conditionService.getByName(name);
    }
}
