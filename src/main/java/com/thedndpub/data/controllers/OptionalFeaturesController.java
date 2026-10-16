package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.optionalfeature.OptionalFeatureDto;
import com.thedndpub.data.dto.optionalfeature.OptionalFeatureSummaryDto;
import com.thedndpub.data.services.OptionalFeaturesService;
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
@RequestMapping("/api/optional-features")
public class OptionalFeaturesController {
    private final OptionalFeaturesService service;

    public OptionalFeaturesController(OptionalFeaturesService service) {
        this.service = service;
    }

    @GetMapping
    public List<OptionalFeatureSummaryDto> find(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String featureType) {
        return service.find(name, source, featureType);
    }

    @GetMapping("/{id}")
    public OptionalFeatureDto byId(@PathVariable String id) {
        return service.byId(id);
    }

    @GetMapping("/by-name/{name}")
    public List<OptionalFeatureDto> byName(
            @PathVariable String name,
            @RequestParam(required = false) String source,
            HttpServletRequest request) {
        String prefix = "/api/optional-features/by-name/";
        String uri = request.getRequestURI();
        String decodedName = uri.startsWith(prefix)
                ? UriUtils.decode(uri.substring(prefix.length()), StandardCharsets.UTF_8) : name;
        return service.byName(decodedName, source);
    }
}
