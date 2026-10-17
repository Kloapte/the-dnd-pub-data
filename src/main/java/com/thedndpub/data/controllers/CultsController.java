package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.cultboon.CultDto;
import com.thedndpub.data.dto.cultboon.CultSummaryDto;
import com.thedndpub.data.services.CultsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cults")
public final class CultsController {
    private final CultsService service;

    public CultsController(CultsService service) {
        this.service = service;
    }

    @GetMapping
    public List<CultSummaryDto> find(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String type) {
        return service.find(name, source, type);
    }

    @GetMapping("/by-name/{name}")
    public List<CultDto> findByName(
            @PathVariable String name,
            @RequestParam(required = false) String source) {
        return service.findByName(name, source);
    }

    @GetMapping("/{id}")
    public CultDto findById(@PathVariable String id) {
        return service.findById(id);
    }
}
