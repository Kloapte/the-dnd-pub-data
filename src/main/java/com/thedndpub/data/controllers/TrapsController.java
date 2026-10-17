package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.traphazard.TrapDetailDto;
import com.thedndpub.data.dto.traphazard.TrapSummaryDto;
import com.thedndpub.data.services.TrapsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/traps")
public final class TrapsController {
    private final TrapsService service;

    public TrapsController(TrapsService service) {
        this.service = service;
    }

    @GetMapping
    public List<TrapSummaryDto> findAll(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String type) {
        return service.findAll(source, type);
    }

    @GetMapping("/by-name/{name}")
    public List<TrapSummaryDto> findByName(
            @PathVariable String name,
            @RequestParam(required = false) String source) {
        return service.findByName(name, source);
    }

    @GetMapping("/{id}")
    public TrapDetailDto findById(@PathVariable String id) {
        return service.findById(id);
    }
}
