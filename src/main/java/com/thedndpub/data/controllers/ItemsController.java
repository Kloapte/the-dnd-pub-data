package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.item.ItemDto;
import com.thedndpub.data.dto.item.ItemSummaryDto;
import com.thedndpub.data.services.ItemsService;
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
@RequestMapping("/api/items")
public class ItemsController {
    private final ItemsService service;

    public ItemsController(ItemsService service) {
        this.service = service;
    }

    @GetMapping
    public List<ItemSummaryDto> find(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String rarity) {
        return service.find(name, source, type, rarity);
    }

    @GetMapping("/{id}")
    public ItemDto byId(@PathVariable String id) {
        return service.byId(id);
    }

    @GetMapping("/by-name/{name}")
    public List<ItemDto> byName(
            @PathVariable String name,
            @RequestParam(required = false) String source,
            HttpServletRequest request) {
        String prefix = "/api/items/by-name/";
        String uri = request.getRequestURI();
        String requestedName = uri.startsWith(prefix)
                ? UriUtils.decode(uri.substring(prefix.length()), StandardCharsets.UTF_8) : name;
        return service.byName(requestedName, source);
    }
}
