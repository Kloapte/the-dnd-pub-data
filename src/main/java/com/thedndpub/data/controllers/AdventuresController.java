package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.adventure.AdventureDto;
import com.thedndpub.data.dto.adventure.AdventureSummaryDto;
import com.thedndpub.data.services.AdventuresService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/adventures")
public final class AdventuresController {
    private final AdventuresService service;

    public AdventuresController(AdventuresService service) {
        this.service = service;
    }

    @GetMapping
    public List<AdventureSummaryDto> findAll(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String group) {
        return service.findAll(source, group);
    }

    @GetMapping("/by-name/{name}")
    public List<AdventureSummaryDto> findByName(
            @PathVariable String name,
            @RequestParam(required = false) String source) {
        return service.findByName(name, source);
    }

    @GetMapping("/{id}")
    public AdventureDto findById(@PathVariable String id) {
        return service.findById(id);
    }
}
