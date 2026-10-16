package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.classes.ClassFeatureDto;
import com.thedndpub.data.dto.classes.ClassFeatureSummaryDto;
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
@RequestMapping("/api/class-features")
public final class ClassFeaturesController {
    private final ClassesService service;
    public ClassFeaturesController(ClassesService service) {
        this.service = service;
    }

    @GetMapping
    public List<ClassFeatureSummaryDto> find(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String classId,
            @RequestParam(required = false) Integer level) {
        return service.findClassFeatures(name, source, classId, level);
    }

    @GetMapping("/{id}")
    public ClassFeatureDto byId(@PathVariable String id) {
        return service.classFeatureById(id);
    }

    @GetMapping("/by-name/{name}")
    public List<ClassFeatureDto> byName(@PathVariable String name,
            @RequestParam(required = false) String source, HttpServletRequest request) {
        return service.classFeaturesByName(decodeName(name, request), source);
    }

    private static String decodeName(String name, HttpServletRequest request) {
        String prefix = "/api/class-features/by-name/";
        String uri = request.getRequestURI();
        return uri.startsWith(prefix) ? UriUtils.decode(uri.substring(prefix.length()), StandardCharsets.UTF_8) : name;
    }
}
