package com.thedndpub.data.controllers.rule;

import com.thedndpub.data.dto.rule.RuleRecord;
import com.thedndpub.data.services.rule.RuleService;
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
@RequestMapping("/api/rules")
@Tag(name = "Rules", description = "Canonical rules and mechanics")
public class RuleController {
    private final RuleService ruleService;

    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @GetMapping
    @Operation(summary = "Get all rules")
    @ApiResponse(responseCode = "200", description = "Rules in normalized dataset order")
    public List<RuleRecord> getAll() {
        return ruleService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a rule by its exact identifier")
    @ApiResponse(responseCode = "404", description = "No rule has this identifier")
    public RuleRecord getById(@Parameter(description = "Exact rule identifier") @PathVariable String id) {
        return ruleService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rule not found: " + id));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get rules by exact name")
    @ApiResponse(responseCode = "200", description = "All rules with this exact name; names may be duplicated")
    public List<RuleRecord> getByName(@Parameter(description = "Exact rule name") @PathVariable String name) {
        return ruleService.getByName(name);
    }
}
