package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.bastion.FacilityDetailDto;
import com.thedndpub.data.dto.bastion.FacilitySummaryDto;
import com.thedndpub.data.services.BastionFacilitiesService;
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
@RequestMapping("/api/bastion-facilities")
public final class BastionFacilitiesController {
    private final BastionFacilitiesService service;

    public BastionFacilitiesController(BastionFacilitiesService service) {
        this.service = service;
    }

    @GetMapping
    public List<FacilitySummaryDto> find(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer level) {
        return service.find(source, type, level);
    }

    @GetMapping("/{id}")
    public FacilityDetailDto byId(@PathVariable String id) {
        return service.byId(id);
    }

    @GetMapping("/by-name/{name}")
    public List<FacilityDetailDto> byName(
            @PathVariable String name,
            @RequestParam(required = false) String source,
            HttpServletRequest request) {
        String prefix = "/api/bastion-facilities/by-name/";
        String uri = request.getRequestURI();
        String requestedName = uri.startsWith(prefix)
                ? UriUtils.decode(uri.substring(prefix.length()), StandardCharsets.UTF_8)
                : name;
        return service.byName(requestedName, source);
    }
}
