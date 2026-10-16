package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.sense.SenseDto;
import com.thedndpub.data.dto.sense.SenseSummaryDto;
import com.thedndpub.data.services.SensesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/senses")
public final class SensesController {
    private final SensesService sensesService;

    public SensesController(SensesService sensesService) {
        this.sensesService = sensesService;
    }

    @GetMapping
    public List<SenseSummaryDto> findSenses(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String source) {
        return sensesService.findSenses(name, source);
    }

    @GetMapping("/{id}")
    public SenseDto findById(@PathVariable String id) {
        return sensesService.findById(id);
    }

    @GetMapping("/by-name/{name}")
    public List<SenseDto> findByName(
            @PathVariable String name,
            @RequestParam(required = false) String source) {
        return sensesService.findByName(name, source);
    }
}
