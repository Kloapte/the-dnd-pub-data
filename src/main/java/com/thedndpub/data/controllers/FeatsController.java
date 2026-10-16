package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.feat.FeatDto;
import com.thedndpub.data.dto.feat.FeatSummaryDto;
import com.thedndpub.data.services.FeatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;
import jakarta.servlet.http.HttpServletRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/feats")
public class FeatsController {
    private final FeatsService featsService;

    public FeatsController(FeatsService featsService) {
        this.featsService = featsService;
    }

    @GetMapping
    public List<FeatSummaryDto> findFeats(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String category) {
        return featsService.findFeats(name, source, category);
    }

    @GetMapping("/{id}")
    public FeatDto findById(@PathVariable String id) {
        return featsService.findById(id);
    }

    @GetMapping("/by-name/{name}")
    public List<FeatDto> findByName(
            @PathVariable String name,
            @RequestParam(required = false) String source,
            HttpServletRequest request) {
        String marker = "/api/feats/by-name/";
        String requestUri = request.getRequestURI();
        String requestedName = requestUri.startsWith(marker)
                ? UriUtils.decode(requestUri.substring(marker.length()), StandardCharsets.UTF_8)
                : name;
        return featsService.findByName(requestedName, source);
    }
}
