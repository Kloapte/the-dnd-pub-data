package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.encounter.EncounterDefinition;
import com.thedndpub.data.dto.encounter.EncounterMapper;
import com.thedndpub.data.dto.encounter.EncounterSummaryDto;
import com.thedndpub.data.dte.encounter.EncounterDte;
import com.thedndpub.data.dte.encounter.EncounterFileDte;
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
public final class EncounterCatalog {
    private static final String RESOURCE = "data/encounters.json";
    private final Map<String, EncounterDefinition> byId;
    private final Map<String, List<EncounterDefinition>> byName;
    private final List<EncounterSummaryDto> summaries;

    @Autowired
    public EncounterCatalog(ObjectMapper mapper, EncounterMapper encounterMapper, SourceRegistry sourceRegistry) {
        this(load(mapper), encounterMapper, sourceRegistry);
    }

    public EncounterCatalog(EncounterFileDte source, EncounterMapper mapper, SourceRegistry registry) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(mapper, "mapper");
        Objects.requireNonNull(registry, "registry");
        if (source.encounter() == null || source.encounter().isEmpty()) {
            throw new IllegalStateException("Encounter source contains no definitions");
        }

        Map<String, EncounterDefinition> ids = new TreeMap<>();
        Map<String, List<EncounterDefinition>> names = new TreeMap<>();
        List<EncounterSummaryDto> summaryList = new ArrayList<>();
        for (EncounterDte encounter : source.encounter()) {
            if (encounter == null || registry.find(encounter.source()) == null) {
                throw new IllegalStateException("Encounter has missing or unknown authored source identity");
            }
            EncounterDefinition definition = mapper.map(encounter);
            if (ids.putIfAbsent(definition.id().value(), definition) != null) {
                throw new IllegalStateException("Duplicate encounter ID: " + definition.id().value());
            }
            names.computeIfAbsent(normalize(definition.name()), ignored -> new ArrayList<>()).add(definition);
            summaryList.add(EncounterSummaryDto.from(definition));
        }
        summaryList.sort(Comparator.comparing(EncounterSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(value -> value.source().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(value -> value.id().value()));
        byId = Collections.unmodifiableMap(ids);
        byName = immutableNameIndex(names);
        summaries = List.copyOf(summaryList);
    }

    public List<EncounterDefinition> definitions() {
        return List.copyOf(byId.values());
    }

    public List<EncounterSummaryDto> summaries() {
        return summaries;
    }

    public EncounterDefinition findById(String id) {
        return byId.get(id);
    }

    public List<EncounterDefinition> findByName(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        return byName.getOrDefault(normalize(name), List.of());
    }

    public int size() {
        return byId.size();
    }

    private static EncounterFileDte load(ObjectMapper mapper) {
        try (InputStream input = new ClassPathResource(RESOURCE).getInputStream()) {
            return mapper.readerFor(EncounterFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load encounter resource " + RESOURCE, exception);
        }
    }

    private static Map<String, List<EncounterDefinition>> immutableNameIndex(
            Map<String, List<EncounterDefinition>> values) {
        Map<String, List<EncounterDefinition>> result = new TreeMap<>();
        values.forEach((name, matches) -> result.put(name, matches.stream().sorted(
                Comparator.comparing((EncounterDefinition item) -> item.source().identity().code(),
                        String.CASE_INSENSITIVE_ORDER).thenComparing(item -> item.id().value())).toList()));
        return Collections.unmodifiableMap(result);
    }

    private static String normalize(String name) {
        return Normalizer.normalize(name.trim(), Normalizer.Form.NFC).toLowerCase(Locale.ROOT);
    }
}
