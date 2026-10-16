package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.classes.SubclassFeatureDto;
import com.thedndpub.data.dto.classes.SubclassFeatureSummaryDto;
import com.thedndpub.data.services.ClassesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.util.UriUtils;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/subclass-features")
public final class SubclassFeaturesController {
    private final ClassesService service;
    public SubclassFeaturesController(ClassesService service) {
        this.service = service;
    }

    @GetMapping
    public List<SubclassFeatureSummaryDto> find(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String classId,
            @RequestParam(required = false) String subclassId,
            @RequestParam(required = false) Integer level) {
        return service.findSubclassFeatures(name, source, classId, subclassId, level);
    }

    @GetMapping("/{id}")
    public SubclassFeatureDto byId(@PathVariable String id) {
        return service.subclassFeatureById(id);
    }

    @GetMapping("/by-name/{name}")
    public List<SubclassFeatureDto> byName(@PathVariable String name,
            @RequestParam(required = false) String source, HttpServletRequest request) {
        return service.subclassFeaturesByName(decodeName(name, request), source);
    }

    private static String decodeName(String name, HttpServletRequest request) {
        String prefix = "/api/subclass-features/by-name/";
        String uri = request.getRequestURI();
        return uri.startsWith(prefix) ? UriUtils.decode(uri.substring(prefix.length()), StandardCharsets.UTF_8) : name;
    }
}
