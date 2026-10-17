package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.cultboon.BoonDto;
import com.thedndpub.data.dto.cultboon.BoonSummaryDto;
import com.thedndpub.data.services.BoonsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/boons")
public final class BoonsController {
    private final BoonsService service;

    public BoonsController(BoonsService service) {
        this.service = service;
    }

    @GetMapping
    public List<BoonSummaryDto> find(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String type) {
        return service.find(name, source, type);
    }

    @GetMapping("/by-name/{name}")
    public List<BoonDto> findByName(
            @PathVariable String name,
            @RequestParam(required = false) String source) {
        return service.findByName(name, source);
    }

    @GetMapping("/{id}")
    public BoonDto findById(@PathVariable String id) {
        return service.findById(id);
    }
}
