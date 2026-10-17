package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.skill.SkillDefinition;
import com.thedndpub.data.dto.skill.SkillImportRecord;
import com.thedndpub.data.dto.skill.SkillImportProvenance;
import com.thedndpub.data.dto.skill.SkillMapper;
import com.thedndpub.data.dto.skill.SkillSummaryDto;
import com.thedndpub.data.dte.skill.SkillDte;
import com.thedndpub.data.dte.skill.SkillsFileDte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/** Loads, validates, and indexes the complete Skills corpus once at startup. */
@Component
public final class SkillCatalog {
    private static final String RESOURCE_PATH = "data/skills.json";

    private final Map<String, SkillDefinition> definitionsById;
    private final Map<String, SkillDefinition> definitionsByNameAndSource;
    private final Map<String, List<SkillDefinition>> definitionsByName;
    private final Map<String, SkillImportProvenance> provenanceById;
    private final List<SkillSummaryDto> summaries;
    private final Set<String> sourceCodes;

    @Autowired
    public SkillCatalog(ObjectMapper objectMapper, SkillMapper mapper, SourceRegistry sourceRegistry) {
        this(load(objectMapper), mapper, sourceRegistry);
    }

    public SkillCatalog(SkillsFileDte source, SkillMapper mapper, SourceRegistry sourceRegistry) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(mapper, "mapper");
        Objects.requireNonNull(sourceRegistry, "sourceRegistry");
        if (source.skill() == null || source.skill().isEmpty()) {
            throw new IllegalStateException("Skills source contains no skill definitions");
        }

        Map<String, SkillDefinition> idIndex = new TreeMap<>();
        Map<String, SkillDefinition> identityIndex = new TreeMap<>();
        Map<String, SkillImportProvenance> provenanceIndex = new TreeMap<>();
        Map<String, List<SkillDefinition>> nameIndex = new TreeMap<>();
        Set<String> sourceIndex = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        List<SkillSummaryDto> summaryList = new ArrayList<>(source.skill().size());

        for (SkillDte skill : source.skill()) {
            SkillImportRecord imported = mapper.mapRecord(skill);
            SkillDefinition definition = imported.definition();
            String sourceCode = definition.source().identity().code();
            requireRegisteredSource(sourceRegistry, sourceCode);

            String id = definition.id().value();
            String identity = canonicalIdentity(definition.name(), sourceCode);
            if (idIndex.putIfAbsent(id, definition) != null) {
                throw new IllegalStateException("Duplicate skill ID: " + id);
            }
            if (identityIndex.putIfAbsent(identity, definition) != null) {
                throw new IllegalStateException("Duplicate skill name/source identity: " + identity);
            }

            nameIndex.computeIfAbsent(normalize(definition.name()), ignored -> new ArrayList<>())
                    .add(definition);
            sourceIndex.add(sourceCode);
            provenanceIndex.put(id, imported.provenance());
            summaryList.add(SkillSummaryDto.from(definition));
        }

        validateReprintReferences(identityIndex, provenanceIndex, sourceRegistry);
        summaryList.sort(Comparator
                .comparing(SkillSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.source().identity().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.id().value()));

        Map<String, List<SkillDefinition>> immutableNameIndex = new TreeMap<>();
        nameIndex.forEach((name, definitions) -> {
            definitions.sort(Comparator
                    .comparing((SkillDefinition definition) -> definition.source().identity().code(),
                            String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(definition -> definition.id().value()));
            immutableNameIndex.put(name, List.copyOf(definitions));
        });
        definitionsById = Collections.unmodifiableMap(idIndex);
        definitionsByNameAndSource = Collections.unmodifiableMap(identityIndex);
        definitionsByName = Collections.unmodifiableMap(immutableNameIndex);
        provenanceById = Collections.unmodifiableMap(provenanceIndex);
        summaries = List.copyOf(summaryList);
        sourceCodes = Collections.unmodifiableSet(sourceIndex);
    }

    public List<SkillSummaryDto> summaries() {
        return summaries;
    }

    public SkillDefinition findById(String id) {
        return definitionsById.get(id);
    }

    public List<SkillDefinition> definitions() {
        return List.copyOf(definitionsById.values());
    }

    public SkillDefinition findByNameAndSource(String name, String source) {
        return definitionsByNameAndSource.get(canonicalIdentity(name, source));
    }

    public List<SkillDefinition> findByName(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        return definitionsByName.getOrDefault(normalize(name), List.of());
    }

    public SkillImportProvenance provenanceById(String id) {
        return provenanceById.get(id);
    }

    public int size() {
        return definitionsById.size();
    }

    public Set<String> sourceCodes() {
        return sourceCodes;
    }

    private static SkillsFileDte load(ObjectMapper objectMapper) {
        try (InputStream input = new ClassPathResource(RESOURCE_PATH).getInputStream()) {
            return objectMapper.readerFor(SkillsFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load skills resource " + RESOURCE_PATH, exception);
        }
    }

    private static void validateReprintReferences(
            Map<String, SkillDefinition> definitionsByIdentity,
            Map<String, SkillImportProvenance> provenanceById,
            SourceRegistry sourceRegistry) {
        for (SkillDefinition definition : definitionsByIdentity.values()) {
            SkillImportProvenance provenance = provenanceById.get(definition.id().value());
            for (String reprint : provenance.reprintedAs()) {
                int separator = reprint.lastIndexOf('|');
                if (separator < 1 || separator == reprint.length() - 1) {
                    throw new IllegalStateException("Malformed skill reprint reference: " + reprint);
                }

                String targetName = reprint.substring(0, separator).trim();
                String targetSource = reprint.substring(separator + 1).trim();
                requireRegisteredSource(sourceRegistry, targetSource);
                if (!definitionsByIdentity.containsKey(canonicalIdentity(targetName, targetSource))) {
                    throw new IllegalStateException("Unresolved skill reprint reference: " + reprint);
                }
            }
        }
    }

    private static void requireRegisteredSource(SourceRegistry registry, String sourceCode) {
        if (registry.find(sourceCode) == null) {
            throw new IllegalStateException("Skill source code is missing from Source Registry: " + sourceCode);
        }
    }

    private static String canonicalIdentity(String name, String source) {
        return normalize(name) + "\u0000" + normalize(source);
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
