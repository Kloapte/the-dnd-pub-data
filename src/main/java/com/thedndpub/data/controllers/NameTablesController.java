package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.nametable.NameTableDto;
import com.thedndpub.data.dto.nametable.NameTableSummaryDto;
import com.thedndpub.data.services.NameTablesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/name-tables")
public final class NameTablesController {
    private final NameTablesService service;

    public NameTablesController(NameTablesService service) {
        this.service = service;
    }

    @GetMapping
    public List<NameTableSummaryDto> findAll(@RequestParam(required = false) String source) {
        return service.findAll(source);
    }

    @GetMapping("/by-name/{name}")
    public List<NameTableSummaryDto> findByName(
            @PathVariable String name,
            @RequestParam(required = false) String source) {
        return service.findByName(name, source);
    }

    @GetMapping("/{id}")
    public NameTableDto findById(@PathVariable String id) {
        return service.findById(id);
    }
}
