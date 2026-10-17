package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.deity.DeityDetailDto;
import com.thedndpub.data.dto.deity.DeitySummaryDto;
import com.thedndpub.data.services.DeitiesService;
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
@RequestMapping("/api/deities")
public final class DeitiesController {
    private final DeitiesService service;

    public DeitiesController(DeitiesService service) {
        this.service = service;
    }

    @GetMapping
    public List<DeitySummaryDto> find(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String pantheon,
            @RequestParam(required = false) String alignment,
            @RequestParam(required = false) String domain) {
        return service.find(name, source, pantheon, alignment, domain);
    }

    @GetMapping("/{id}")
    public DeityDetailDto byId(@PathVariable String id) {
        return service.byId(id);
    }

    @GetMapping("/by-name/{name}")
    public List<DeityDetailDto> byName(
            @PathVariable String name,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String pantheon,
            HttpServletRequest request) {
        String prefix = "/api/deities/by-name/";
        String uri = request.getRequestURI();
        String requestedName = uri.startsWith(prefix)
                ? UriUtils.decode(uri.substring(prefix.length()), StandardCharsets.UTF_8)
                : name;
        return service.byName(requestedName, source, pantheon);
    }
}
