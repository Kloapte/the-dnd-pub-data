package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.race.RaceDto;
import com.thedndpub.data.dto.race.RaceSummaryDto;
import com.thedndpub.data.services.RacesService;
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
@RequestMapping("/api/races")
public class RacesController {
    private final RacesService service;

    public RacesController(RacesService service) {
        this.service = service;
    }

    @GetMapping
    public List<RaceSummaryDto> find(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String definitionKind) {
        return service.find(name, source, definitionKind);
    }

    @GetMapping("/{id}")
    public RaceDto byId(@PathVariable String id) {
        return service.byId(id);
    }

    @GetMapping("/by-name/{name}")
    public List<RaceDto> byName(
            @PathVariable String name,
            @RequestParam(required = false) String source,
            HttpServletRequest request) {
        String prefix = "/api/races/by-name/";
        String uri = request.getRequestURI();
        String decodedName = uri.startsWith(prefix)
                ? UriUtils.decode(uri.substring(prefix.length()), StandardCharsets.UTF_8)
                : name;
        return service.byName(decodedName, source);
    }
}
