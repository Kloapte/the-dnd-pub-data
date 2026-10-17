package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.recipe.RecipeDetailDto;
import com.thedndpub.data.dto.recipe.RecipeSummaryDto;
import com.thedndpub.data.services.RecipesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public final class RecipesController {
    private final RecipesService service;

    public RecipesController(RecipesService service) {
        this.service = service;
    }

    @GetMapping
    public List<RecipeSummaryDto> findAll(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String dishType) {
        return service.findAll(source, type, dishType);
    }

    @GetMapping("/by-name/{name}")
    public List<RecipeSummaryDto> findByName(
            @PathVariable String name,
            @RequestParam(required = false) String source) {
        return service.findByName(name, source);
    }

    @GetMapping("/{id}")
    public RecipeDetailDto findById(@PathVariable String id) {
        return service.findById(id);
    }
}
