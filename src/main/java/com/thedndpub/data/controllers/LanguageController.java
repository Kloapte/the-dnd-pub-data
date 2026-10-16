package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.language.LanguageDto;
import com.thedndpub.data.dto.language.LanguageSummaryDto;
import com.thedndpub.data.services.LanguagesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/languages")
public class LanguageController {
    private final LanguagesService languagesService;

    public LanguageController(LanguagesService languagesService) {
        this.languagesService = languagesService;
    }

    @GetMapping
    public List<LanguageSummaryDto> findLanguages(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String source) {
        return languagesService.findLanguages(name, source);
    }

    @GetMapping("/{id}")
    public LanguageDto findById(@PathVariable String id) {
        return languagesService.findById(id);
    }

    @GetMapping("/by-name/{name}")
    public LanguageDto findByNameAndSource(
            @PathVariable String name,
            @RequestParam String source) {
        return languagesService.findByNameAndSource(name, source);
    }
}
