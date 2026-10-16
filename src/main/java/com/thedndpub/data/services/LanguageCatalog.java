package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.thedndpub.data.dto.language.LanguageMapper;
import com.thedndpub.data.dto.language.LanguageImportRecord;
import com.thedndpub.data.dto.language.LanguageImportProvenance;
import com.thedndpub.data.dto.language.LanguageSummaryDto;
import com.thedndpub.data.dto.language.NormalizedLanguageDefinition;
import com.thedndpub.data.dte.language.LanguageDte;
import com.thedndpub.data.dte.language.LanguagesFileDte;
import com.thedndpub.data.dte.language.LanguageScriptDte;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@Component
public final class LanguageCatalog {
    private static final String RESOURCE_PATH = "data/languages.json";

    private final Map<String, NormalizedLanguageDefinition> definitionsById;
    private final Map<String, NormalizedLanguageDefinition> definitionsByNameAndSource;
    private final List<LanguageSummaryDto> summaries;
    private final Map<String, LanguageImportProvenance> provenanceById;
    private final int sourceRecordCount;
    private final int scriptRecordCount;

    @Autowired
    public LanguageCatalog(ObjectMapper objectMapper, LanguageMapper mapper, SourceRegistry sourceRegistry) {
        this(load(objectMapper), mapper, sourceRegistry);
    }

    public LanguageCatalog(LanguagesFileDte source, LanguageMapper mapper) {
        this(source, mapper, null);
    }

    public LanguageCatalog(LanguagesFileDte source, LanguageMapper mapper, SourceRegistry sourceRegistry) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(mapper, "mapper");
        List<LanguageDte> languages = source.language();
        List<LanguageScriptDte> scripts = source.languageScript();
        if (languages == null || languages.isEmpty()) {
            throw new IllegalStateException("Language source contains no language definitions");
        }
        if (scripts == null) {
            scripts = List.of();
        }

        Map<String, NormalizedLanguageDefinition> idIndex = new TreeMap<>();
        Map<String, NormalizedLanguageDefinition> identityIndex = new TreeMap<>();
        Map<String, LanguageImportProvenance> provenanceIndex = new TreeMap<>();
        List<LanguageSummaryDto> summaryList = new ArrayList<>(languages.size());
        for (LanguageDte language : languages) {
            if (sourceRegistry != null) {
                requireSource(sourceRegistry, language.source());
                validateReferences(sourceRegistry, language);
            }
            LanguageImportRecord imported = mapper.mapRecord(language);
            NormalizedLanguageDefinition definition = imported.definition();
            String id = definition.id().value();
            String identity = canonicalIdentity(definition.name(), definition.source().code());
            if (idIndex.putIfAbsent(id, definition) != null) {
                throw new IllegalStateException("Duplicate language ID: " + id);
            }
            if (identityIndex.putIfAbsent(identity, definition) != null) {
                throw new IllegalStateException("Duplicate language name/source identity: " + identity);
            }
            summaryList.add(LanguageSummaryDto.from(definition));
            provenanceIndex.put(id, imported.provenance());
        }

        summaryList.sort(Comparator
                .comparing(LanguageSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(LanguageSummaryDto::source, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.id().value()));
        validateScripts(scripts);
        if (sourceRegistry != null) {
            for (LanguageScriptDte script : scripts) {
                requireSource(sourceRegistry, script.source());
            }
        }

        definitionsById = Collections.unmodifiableMap(idIndex);
        definitionsByNameAndSource = Collections.unmodifiableMap(identityIndex);
        provenanceById = Collections.unmodifiableMap(provenanceIndex);
        summaries = List.copyOf(summaryList);
        sourceRecordCount = languages.size();
        scriptRecordCount = scripts.size();
    }

    public List<LanguageSummaryDto> summaries() {
        return summaries;
    }

    public NormalizedLanguageDefinition findById(String id) {
        return definitionsById.get(id);
    }

    public NormalizedLanguageDefinition findByNameAndSource(String name, String source) {
        return definitionsByNameAndSource.get(canonicalIdentity(name, source));
    }

    public LanguageImportProvenance provenanceById(String id) {
        return provenanceById.get(id);
    }

    public int size() {
        return definitionsById.size();
    }

    public int sourceRecordCount() {
        return sourceRecordCount;
    }

    public int scriptRecordCount() {
        return scriptRecordCount;
    }

    private static LanguagesFileDte load(ObjectMapper objectMapper) {
        try (InputStream input = new ClassPathResource(RESOURCE_PATH).getInputStream()) {
            return objectMapper.readerFor(LanguagesFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load language resource " + RESOURCE_PATH, exception);
        }
    }

    private static void validateScripts(List<LanguageScriptDte> scripts) {
        Map<String, LanguageScriptDte> scriptIndex = new TreeMap<>();
        for (LanguageScriptDte script : scripts) {
            if (script.name() == null || script.source() == null || script.fonts() == null) {
                throw new IllegalStateException("Language script is missing required source fields");
            }
            String key = canonicalIdentity(script.name(), script.source());
            if (scriptIndex.putIfAbsent(key, script) != null) {
                throw new IllegalStateException("Duplicate language script name/source identity: " + key);
            }
        }
    }

    private static String canonicalIdentity(String name, String source) {
        return name.trim().toLowerCase(Locale.ROOT) + "\u0000" + source.trim().toLowerCase(Locale.ROOT);
    }

    private static void validateReferences(SourceRegistry registry, LanguageDte language) {
        if (language.additionalSources() != null) {
            language.additionalSources().forEach(reference -> requireSource(registry, reference.source()));
        }
        if (language.otherSources() != null) {
            language.otherSources().forEach(reference -> requireSource(registry, reference.source()));
        }
        if (language.reprintedAs() != null) {
            language.reprintedAs().forEach(reference -> {
                int separator = reference.lastIndexOf('|');
                if (separator >= 0 && separator < reference.length() - 1) {
                    requireSource(registry, reference.substring(separator + 1));
                }
            });
        }
    }

    private static void requireSource(SourceRegistry registry, String sourceCode) {
        if (registry.find(sourceCode) == null) {
            throw new IllegalStateException("Source code is missing from Source Registry: " + sourceCode);
        }
    }
}
