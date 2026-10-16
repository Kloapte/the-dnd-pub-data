package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.condition.ConditionDto;
import com.thedndpub.data.dto.condition.ConditionSummaryDto;
import com.thedndpub.data.services.ConditionsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/conditions")
public class ConditionsController {
    private final ConditionsService conditionsService;

    public ConditionsController(ConditionsService conditionsService) {
        this.conditionsService = conditionsService;
    }

    @GetMapping
    public List<ConditionSummaryDto> findConditions(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String source) {
        return conditionsService.findConditions(name, source);
    }

    @GetMapping("/{id}")
    public ConditionDto findById(@PathVariable String id) {
        return conditionsService.findById(id);
    }

    @GetMapping("/by-name/{name}")
    public List<ConditionDto> findByName(
            @PathVariable String name,
            @RequestParam(required = false) String source) {
        return conditionsService.findByName(name, source);
    }
}
