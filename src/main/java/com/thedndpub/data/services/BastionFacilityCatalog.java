package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.bastion.FacilityDefinition;
import com.thedndpub.data.dto.bastion.FacilityMapper;
import com.thedndpub.data.dto.bastion.FacilitySummaryDto;
import com.thedndpub.data.dte.bastion.FacilityDte;
import com.thedndpub.data.dte.bastion.FacilityFileDte;
import com.thedndpub.data.dte.bastion.FacilityDte;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public final class BastionFacilityCatalog {
    private static final String RESOURCE = "data/bastions.json";
    private final Map<String, FacilityDefinition> byId;
    private final Map<String, List<FacilityDefinition>> byName;
    private final Map<String, FacilityDte> sourceById;
    private final List<FacilitySummaryDto> summaries;

    @Autowired
    public BastionFacilityCatalog(ObjectMapper objectMapper, SourceRegistry sourceRegistry) {
        this(load(objectMapper), new FacilityMapper(), sourceRegistry);
    }

    public BastionFacilityCatalog(FacilityFileDte source, FacilityMapper mapper, SourceRegistry sourceRegistry) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(mapper, "mapper");
        Objects.requireNonNull(sourceRegistry, "sourceRegistry");
        if (source.facility() == null || source.facility().isEmpty()) {
            throw new IllegalStateException("Bastion Facility source contains no definitions");
        }

        Map<String, FacilityDefinition> ids = new TreeMap<>();
        Map<String, FacilityDefinition> identities = new TreeMap<>();
        Map<String, List<FacilityDefinition>> names = new TreeMap<>();
        Map<String, FacilityDte> originals = new TreeMap<>();
        List<FacilitySummaryDto> summaryList = new ArrayList<>(source.facility().size());
        for (FacilityDte record : source.facility()) {
            validateRecord(record, sourceRegistry);
            FacilityDefinition definition = mapper.map(record);
            putUnique(ids, definition.id(), definition, "Bastion Facility ID");
            String identity = normalize(record.source()) + "\u0000" + normalize(record.name());
            putUnique(identities, identity, definition, "Bastion Facility source/name identity");
            names.computeIfAbsent(normalize(definition.name()), ignored -> new ArrayList<>()).add(definition);
            originals.put(definition.id(), record);
            summaryList.add(FacilitySummaryDto.from(definition));
        }

        summaryList.sort(Comparator.comparing(FacilitySummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(value -> value.source().identity().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(FacilitySummaryDto::id));
        byId = Collections.unmodifiableMap(ids);
        byName = immutableNameIndex(names);
        sourceById = Collections.unmodifiableMap(originals);
        summaries = List.copyOf(summaryList);
    }

    public List<FacilityDefinition> definitions() {
        return List.copyOf(byId.values());
    }

    public List<FacilitySummaryDto> summaries() {
        return summaries;
    }

    public FacilityDefinition findById(String id) {
        return byId.get(id);
    }

    public FacilityDte sourceById(String id) {
        return sourceById.get(id);
    }

    public List<FacilityDefinition> findByName(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        return byName.getOrDefault(normalize(name), List.of());
    }

    public int size() {
        return byId.size();
    }

    private static FacilityFileDte load(ObjectMapper mapper) {
        try (InputStream input = new ClassPathResource(RESOURCE).getInputStream()) {
            return mapper.readerFor(FacilityFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load Bastion Facility resource " + RESOURCE, exception);
        }
    }

    private static void validateRecord(FacilityDte record, SourceRegistry registry) {
        if (record == null || record.name() == null || record.name().isBlank()
                || record.source() == null || record.source().isBlank()) {
            throw new IllegalStateException("Bastion Facility has no authored name/source identity");
        }
        if (registry.find(record.source()) == null) {
            throw new IllegalStateException("Bastion Facility references unknown source code: " + record.source());
        }
        FacilitySourceMarkupValidator.validate(record, registry);
        if (!List.of("basic", "special").contains(record.facilityType())) {
            throw new IllegalStateException("Unsupported Bastion Facility type: " + record.facilityType());
        }
    }

    private static void putUnique(Map<String, FacilityDefinition> index, String key,
            FacilityDefinition value, String identityType) {
        if (index.putIfAbsent(key, value) != null) {
            throw new IllegalStateException("Duplicate " + identityType + ": " + key);
        }
    }

    private static Map<String, List<FacilityDefinition>> immutableNameIndex(
            Map<String, List<FacilityDefinition>> values) {
        Map<String, List<FacilityDefinition>> immutable = new TreeMap<>();
        values.forEach((name, matches) -> immutable.put(name, matches.stream()
                .sorted(Comparator.comparing((FacilityDefinition value) -> value.source().identity().code(),
                        String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(FacilityDefinition::id))
                .toList()));
        return Collections.unmodifiableMap(immutable);
    }

    private static String normalize(String value) {
        return Normalizer.normalize(value.trim(), Normalizer.Form.NFC).toLowerCase(Locale.ROOT);
    }
}
