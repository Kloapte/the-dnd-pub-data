package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.background.BackgroundDto;
import com.thedndpub.data.dto.background.BackgroundSummaryDto;
import com.thedndpub.data.services.BackgroundsService;
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
@RequestMapping("/api/backgrounds")
public class BackgroundsController {
    private final BackgroundsService service;

    public BackgroundsController(BackgroundsService service) {
        this.service = service;
    }

    @GetMapping
    public List<BackgroundSummaryDto> find(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String source) {
        return service.find(name, source);
    }

    @GetMapping("/{id}")
    public BackgroundDto byId(@PathVariable String id) {
        return service.byId(id);
    }

    @GetMapping("/by-name/{name}")
    public List<BackgroundDto> byName(
            @PathVariable String name,
            @RequestParam(required = false) String source,
            HttpServletRequest request) {
        String prefix = "/api/backgrounds/by-name/";
        String uri = request.getRequestURI();
        String decodedName = uri.startsWith(prefix)
                ? UriUtils.decode(uri.substring(prefix.length()), StandardCharsets.UTF_8)
                : name;
        return service.byName(decodedName, source);
    }
}
