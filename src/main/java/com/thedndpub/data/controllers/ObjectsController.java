package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.object.ObjectDetailDto;
import com.thedndpub.data.dto.object.ObjectSummaryDto;
import com.thedndpub.data.services.ObjectsService;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

@RestController
@RequestMapping("/api/objects")
public final class ObjectsController {
    private final ObjectsService service;

    public ObjectsController(ObjectsService service) {
        this.service = service;
    }

    @GetMapping
    public List<ObjectSummaryDto> find(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String type) {
        return service.find(name, source, type);
    }

    @GetMapping("/{id}")
    public ObjectDetailDto byId(@PathVariable String id) {
        return service.byId(id);
    }

    @GetMapping("/by-name/{name}")
    public List<ObjectDetailDto> byName(
            @PathVariable String name,
            @RequestParam(required = false) String source,
            HttpServletRequest request) {
        String prefix = "/api/objects/by-name/";
        String uri = request.getRequestURI();
        String requestedName = uri.startsWith(prefix)
                ? UriUtils.decode(uri.substring(prefix.length()), StandardCharsets.UTF_8)
                : name;
        return service.byName(requestedName, source);
    }
}
