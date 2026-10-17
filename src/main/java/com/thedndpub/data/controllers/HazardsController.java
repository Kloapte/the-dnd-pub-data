package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.traphazard.HazardDetailDto;
import com.thedndpub.data.dto.traphazard.HazardSummaryDto;
import com.thedndpub.data.services.HazardsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/hazards")
public final class HazardsController {
    private final HazardsService service;

    public HazardsController(HazardsService service) {
        this.service = service;
    }

    @GetMapping
    public List<HazardSummaryDto> findAll(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String type) {
        return service.findAll(source, type);
    }

    @GetMapping("/by-name/{name}")
    public List<HazardSummaryDto> findByName(
            @PathVariable String name,
            @RequestParam(required = false) String source) {
        return service.findByName(name, source);
    }

    @GetMapping("/{id}")
    public HazardDetailDto findById(@PathVariable String id) {
        return service.findById(id);
    }
}
