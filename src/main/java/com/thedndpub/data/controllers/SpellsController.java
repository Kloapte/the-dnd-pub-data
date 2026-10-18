package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.spell.api.SpellDto;
import com.thedndpub.data.dto.spell.api.SpellSummaryDto;
import com.thedndpub.data.services.SpellsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/spells")
public final class SpellsController {
    private final SpellsService service;

    public SpellsController(SpellsService service) {
        this.service = service;
    }

    @GetMapping
    public List<SpellSummaryDto> find(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) Integer level,
            @RequestParam(required = false) String school,
            @RequestParam(required = false) Boolean ritual,
            @RequestParam(required = false) Boolean concentration,
            @RequestParam(required = false) String damageType) {
        return service.findSpells(name, source, level, school, ritual, concentration, damageType);
    }

    @GetMapping("/{id}")
    public SpellDto byId(@PathVariable String id, @RequestParam(required = false) String include) {
        return service.findById(id, include);
    }

    @GetMapping("/by-name/{name}")
    public List<SpellDto> byName(
            @PathVariable String name,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String include,
            HttpServletRequest request) {
        String marker = "/api/spells/by-name/";
        String requestUri = request.getRequestURI();
        String requestedName = requestUri.startsWith(marker)
                ? UriUtils.decode(requestUri.substring(marker.length()), StandardCharsets.UTF_8)
                : name;
        return service.findByName(requestedName, source, include);
    }
}
