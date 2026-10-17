package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.charoption.CharacterCreationOptionDto;
import com.thedndpub.data.dto.charoption.CharacterCreationOptionSummaryDto;
import com.thedndpub.data.services.CharacterCreationOptionsService;
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
@RequestMapping("/api/character-creation-options")
public final class CharacterCreationOptionsController {
    private static final String BY_NAME_PREFIX = "/api/character-creation-options/by-name/";

    private final CharacterCreationOptionsService service;

    public CharacterCreationOptionsController(CharacterCreationOptionsService service) {
        this.service = service;
    }

    @GetMapping
    public List<CharacterCreationOptionSummaryDto> find(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String optionType) {
        return service.find(name, source, optionType);
    }

    @GetMapping("/{id}")
    public CharacterCreationOptionDto findById(@PathVariable String id) {
        return service.findById(id);
    }

    @GetMapping("/by-name/{name}")
    public List<CharacterCreationOptionDto> findByName(
            @PathVariable String name,
            @RequestParam(required = false) String source,
            HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String decodedName = requestUri.startsWith(BY_NAME_PREFIX)
                ? UriUtils.decode(requestUri.substring(BY_NAME_PREFIX.length()), StandardCharsets.UTF_8)
                : name;
        return service.findByName(decodedName, source);
    }
}
