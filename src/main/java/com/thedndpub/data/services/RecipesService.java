package com.thedndpub.data.services;

import com.thedndpub.data.dto.recipe.RecipeDefinition;
import com.thedndpub.data.dto.recipe.RecipeDetailDto;
import com.thedndpub.data.dto.recipe.RecipeMapper;
import com.thedndpub.data.dto.recipe.RecipeSummaryDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class RecipesService {
    private final RecipeCatalog catalog;
    private final RecipeMapper mapper;

    public RecipesService(RecipeCatalog catalog, RecipeMapper mapper) {
        this.catalog = catalog;
        this.mapper = mapper;
    }

    public List<RecipeSummaryDto> findAll(String source, String type, String dishType) {
        return catalog.summaries().stream()
                .filter(value -> source == null || value.source().code().equalsIgnoreCase(source.trim()))
                .filter(value -> type == null || value.type() != null && value.type().equalsIgnoreCase(type.trim()))
                .filter(value -> dishType == null || value.dishTypes() != null
                        && value.dishTypes().stream().anyMatch(candidate -> candidate.equalsIgnoreCase(dishType.trim())))
                .toList();
    }

    public RecipeDetailDto findById(String id) {
        RecipeDefinition definition = catalog.findById(id);
        if (definition == null) {
            throw new RecipeNotFoundException(id);
        }
        return mapper.detail(definition);
    }

    public List<RecipeSummaryDto> findByName(String name, String source) {
        return catalog.findByName(name).stream()
                .filter(definition -> source == null
                        || definition.source().identity().code().equalsIgnoreCase(source.trim()))
                .map(mapper::summary)
                .toList();
    }
}
