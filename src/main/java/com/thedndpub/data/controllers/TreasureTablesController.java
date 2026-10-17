package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.treasure.TreasureTableDetailDto;
import com.thedndpub.data.dto.treasure.TreasureTableSummaryDto;
import com.thedndpub.data.services.TreasureTablesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/treasure-tables")
public final class TreasureTablesController {
    private final TreasureTablesService service;

    public TreasureTablesController(TreasureTablesService service) {
        this.service = service;
    }

    @GetMapping
    public List<TreasureTableSummaryDto> findAll(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category) {
        return service.findAll(source, type, category);
    }

    @GetMapping("/by-name/{name}")
    public List<TreasureTableSummaryDto> findByName(
            @PathVariable String name,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String type) {
        return service.findByName(name, source, type);
    }

    @GetMapping("/{id}")
    public TreasureTableDetailDto findById(@PathVariable String id) {
        return service.findById(id);
    }
}
