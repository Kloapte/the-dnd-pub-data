package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.recipe.RecipeDefinition;
import com.thedndpub.data.dto.recipe.RecipeMapper;
import com.thedndpub.data.dto.recipe.RecipeSummaryDto;
import com.thedndpub.data.dte.recipe.RecipeCookNoteEntryDte;
import com.thedndpub.data.dte.recipe.RecipeCookNoteSectionDte;
import com.thedndpub.data.dte.recipe.RecipeDte;
import com.thedndpub.data.dte.recipe.RecipeSourceReferenceDte;
import com.thedndpub.data.dte.recipe.RecipesFileDte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@Component
public final class RecipeCatalog {
    private static final String RESOURCE_PATH = "data/recipes.json";

    private final Map<String, RecipeDefinition> definitionsById;
    private final Map<String, List<RecipeDefinition>> definitionsByName;
    private final List<RecipeSummaryDto> summaries;

    @Autowired
    public RecipeCatalog(ObjectMapper objectMapper, RecipeMapper mapper, SourceRegistry sourceRegistry) {
        this(load(objectMapper), mapper, sourceRegistry);
    }

    public RecipeCatalog(RecipesFileDte source, RecipeMapper mapper, SourceRegistry sourceRegistry) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(mapper, "mapper");
        Objects.requireNonNull(sourceRegistry, "sourceRegistry");
        if (source.recipe() == null || source.recipe().isEmpty()) {
            throw new IllegalStateException("Recipe source contains no definitions");
        }

        Map<String, RecipeDefinition> idIndex = new TreeMap<>();
        Map<String, List<RecipeDefinition>> nameIndex = new TreeMap<>();
        List<RecipeSummaryDto> summaryList = new ArrayList<>(source.recipe().size());

        for (RecipeDte authored : source.recipe()) {
            validateDefinition(authored, sourceRegistry);
            String id = stableId(authored.source(), authored.name());
            RecipeDefinition definition = mapper.normalize(authored, id);
            RecipeDefinition previous = idIndex.putIfAbsent(id, definition);
            if (previous != null) {
                throw new IllegalStateException("Duplicate Recipe ID '" + id + "'");
            }
            nameIndex.computeIfAbsent(nameKey(authored.name()), ignored -> new ArrayList<>()).add(definition);
            summaryList.add(mapper.summary(definition));
        }

        Comparator<RecipeSummaryDto> summaryOrder = Comparator
                .comparing(RecipeSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(value -> value.source().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(value -> value.id().value());
        summaryList.sort(summaryOrder);
        Comparator<RecipeDefinition> definitionOrder = Comparator
                .comparing(RecipeDefinition::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(value -> value.source().identity().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(value -> value.id().value());
        nameIndex.values().forEach(definitions -> definitions.sort(definitionOrder));

        Map<String, List<RecipeDefinition>> immutableNames = new TreeMap<>();
        nameIndex.forEach((name, definitions) -> immutableNames.put(name, List.copyOf(definitions)));
        definitionsById = Collections.unmodifiableMap(idIndex);
        definitionsByName = Collections.unmodifiableMap(immutableNames);
        summaries = List.copyOf(summaryList);
    }

    public List<RecipeDefinition> definitions() {
        return List.copyOf(definitionsById.values());
    }

    public List<RecipeSummaryDto> summaries() {
        return summaries;
    }

    public RecipeDefinition findById(String id) {
        return definitionsById.get(id);
    }

    public List<RecipeDefinition> findByName(String name) {
        return definitionsByName.getOrDefault(nameKey(name), List.of());
    }

    private static void validateDefinition(RecipeDte recipe, SourceRegistry registry) {
        if (recipe == null || recipe.name() == null || recipe.name().isBlank()
                || recipe.source() == null || recipe.source().isBlank()) {
            throw new IllegalStateException("Recipe definition requires authored name and source");
        }
        if (recipe.ingredients() == null || recipe.instructions() == null) {
            throw new IllegalStateException("Recipe '" + recipe.name() + "' is missing its authored content arrays");
        }
        requireRegisteredSource(registry, recipe.source());
        if (recipe.otherSources() != null) {
            recipe.otherSources().forEach(reference -> validateSourceReference(registry, reference));
        }
        if (recipe.noteCook() != null) {
            for (RecipeCookNoteEntryDte note : recipe.noteCook()) {
                if (note instanceof RecipeCookNoteSectionDte section) {
                    section.entries().forEach(entry -> {
                        if (entry.source() != null) {
                            requireRegisteredSource(registry, entry.source());
                        }
                    });
                }
            }
        }
    }

    private static void validateSourceReference(SourceRegistry registry, RecipeSourceReferenceDte reference) {
        if (reference == null || reference.source() == null) {
            throw new IllegalStateException("Recipe secondary source reference has no source identity");
        }
        requireRegisteredSource(registry, reference.source());
    }

    private static void requireRegisteredSource(SourceRegistry registry, String source) {
        if (registry.find(source) == null) {
            throw new IllegalStateException("Recipe refers to an unknown authored source identity: " + source);
        }
    }

    private static String stableId(String source, String name) {
        String slug = Normalizer.normalize(name, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        if (slug.isBlank()) {
            throw new IllegalStateException("Recipe name does not produce a stable identity: " + name);
        }
        return "recipe:" + source + ":" + slug;
    }

    private static String nameKey(String name) {
        return name == null ? "" : name.trim().toLowerCase(Locale.ROOT);
    }

    private static RecipesFileDte load(ObjectMapper objectMapper) {
        try (InputStream input = new ClassPathResource(RESOURCE_PATH).getInputStream()) {
            return objectMapper.readerFor(RecipesFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load Recipe resource " + RESOURCE_PATH, exception);
        }
    }
}
