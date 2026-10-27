package com.thedndpub.data.controllers.action;

import com.thedndpub.data.dto.action.ActionRecord;
import com.thedndpub.data.services.action.ActionService;
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
@RequestMapping("/api/actions")
@Tag(name = "Actions", description = "Canonical action mechanics")
public class ActionController {
    private final ActionService actionService;

    public ActionController(ActionService actionService) {
        this.actionService = actionService;
    }

    @GetMapping
    @Operation(summary = "Get all actions")
    @ApiResponse(responseCode = "200", description = "Actions in normalized dataset order")
    public List<ActionRecord> getAll() {
        return actionService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an action by its exact identifier")
    @ApiResponse(responseCode = "404", description = "No action has this identifier")
    public ActionRecord getById(@Parameter(description = "Exact action identifier") @PathVariable String id) {
        return actionService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Action not found: " + id));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get actions by exact name")
    @ApiResponse(responseCode = "200", description = "All actions with this exact name; names may be duplicated")
    public List<ActionRecord> getByName(@Parameter(description = "Exact action name") @PathVariable String name) {
        return actionService.getByName(name);
    }
}
