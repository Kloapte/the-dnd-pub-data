package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.encounter.EncounterDetailDto;
import com.thedndpub.data.dto.encounter.EncounterSummaryDto;
import com.thedndpub.data.services.EncountersService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/encounters")
public final class EncountersController {
    private final EncountersService service;

    public EncountersController(EncountersService service) {
        this.service = service;
    }

    @GetMapping
    public List<EncounterSummaryDto> findAll(@RequestParam(required = false) String source) {
        return service.findAll(source);
    }

    @GetMapping("/by-name/{name}")
    public List<EncounterSummaryDto> findByName(
            @PathVariable String name,
            @RequestParam(required = false) String source) {
        return service.findByName(name, source);
    }

    @GetMapping("/{id}")
    public EncounterDetailDto findById(@PathVariable String id) {
        return service.findById(id);
    }
}
