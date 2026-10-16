package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.action.ActionDto;
import com.thedndpub.data.dto.action.ActionSummaryDto;
import com.thedndpub.data.services.ActionsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/actions")
public class ActionsController {
    private final ActionsService actionsService;

    public ActionsController(ActionsService actionsService) {
        this.actionsService = actionsService;
    }

    @GetMapping
    public List<ActionSummaryDto> findActions(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String source) {
        return actionsService.findActions(name, source);
    }

    @GetMapping("/{id}")
    public ActionDto findById(@PathVariable String id) {
        return actionsService.findById(id);
    }

    @GetMapping("/by-name/{name}")
    public List<ActionDto> findByName(
            @PathVariable String name,
            @RequestParam(required = false) String source) {
        return actionsService.findByName(name, source);
    }
}
