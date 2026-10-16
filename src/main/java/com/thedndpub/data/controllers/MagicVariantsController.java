package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.magicvariant.MagicVariantDto;
import com.thedndpub.data.dto.magicvariant.MagicVariantKind;
import com.thedndpub.data.dto.magicvariant.MagicVariantSummaryDto;
import com.thedndpub.data.services.MagicVariantsService;
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
@RequestMapping("/api/magic-variants")
public final class MagicVariantsController {
    private final MagicVariantsService service;

    public MagicVariantsController(MagicVariantsService service) {
        this.service = service;
    }

    @GetMapping
    public List<MagicVariantSummaryDto> find(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) MagicVariantKind kind) {
        return service.find(name, source, type, kind);
    }

    @GetMapping("/{id}")
    public MagicVariantDto byId(@PathVariable String id) {
        return service.byId(id);
    }

    @GetMapping("/by-name/{name}")
    public List<MagicVariantDto> byName(
            @PathVariable String name,
            @RequestParam(required = false) String source,
            HttpServletRequest request) {
        String prefix = "/api/magic-variants/by-name/";
        String uri = request.getRequestURI();
        String requestedName = uri.startsWith(prefix)
                ? UriUtils.decode(uri.substring(prefix.length()), StandardCharsets.UTF_8)
                : name;
        return service.byName(requestedName, source);
    }
}
