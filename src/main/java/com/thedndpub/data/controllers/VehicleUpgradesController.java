package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.vehicle.VehicleUpgradeDetailDto;
import com.thedndpub.data.dto.vehicle.VehicleUpgradeSummaryDto;
import com.thedndpub.data.services.VehicleUpgradesService;
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
@RequestMapping("/api/vehicle-upgrades")
public final class VehicleUpgradesController {
    private final VehicleUpgradesService service;

    public VehicleUpgradesController(VehicleUpgradesService service) {
        this.service = service;
    }

    @GetMapping
    public List<VehicleUpgradeSummaryDto> find(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String type) {
        return service.find(name, source, type);
    }

    @GetMapping("/{id}")
    public VehicleUpgradeDetailDto byId(@PathVariable String id) {
        return service.byId(id);
    }

    @GetMapping("/by-name/{name}")
    public List<VehicleUpgradeDetailDto> byName(
            @PathVariable String name,
            @RequestParam(required = false) String source,
            HttpServletRequest request) {
        String prefix = "/api/vehicle-upgrades/by-name/";
        String uri = request.getRequestURI();
        String requestedName = uri.startsWith(prefix)
                ? UriUtils.decode(uri.substring(prefix.length()), StandardCharsets.UTF_8)
                : name;
        return service.byName(requestedName, source);
    }
}
