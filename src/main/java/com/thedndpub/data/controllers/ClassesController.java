package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.classes.ClassDto;
import com.thedndpub.data.dto.classes.ClassFeatureSummaryDto;
import com.thedndpub.data.dto.classes.ClassSummaryDto;
import com.thedndpub.data.dto.classes.SubclassSummaryDto;
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
@RequestMapping("/api/classes")
public final class ClassesController {
    private final ClassesService service;
    public ClassesController(ClassesService service) {
        this.service = service;
    }

    @GetMapping
    public List<ClassSummaryDto> find(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String source) {
        return service.findClasses(name, source);
    }

    @GetMapping("/{id}")
    public ClassDto byId(@PathVariable String id) {
        return service.classById(id);
    }

    @GetMapping("/by-name/{name}")
    public List<ClassDto> byName(@PathVariable String name,
            @RequestParam(required = false) String source, HttpServletRequest request) {
        String prefix = "/api/classes/by-name/";
        String uri = request.getRequestURI();
        String decoded = uri.startsWith(prefix)
                ? UriUtils.decode(uri.substring(prefix.length()), StandardCharsets.UTF_8) : name;
        return service.classesByName(decoded, source);
    }
    @GetMapping("/{id}/subclasses")
    public List<SubclassSummaryDto> subclasses(@PathVariable String id) {
        return service.subclassesOf(id);
    }

    @GetMapping("/{id}/features")
    public List<ClassFeatureSummaryDto> features(@PathVariable String id) {
        return service.featuresOfClass(id);
    }
}
