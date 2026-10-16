package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.book.PublicationDefinition;
import com.thedndpub.data.dto.source.PublicationMetadataStatus;
import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.source.SourceMetadata;
import com.thedndpub.data.dto.source.SourceRecord;
import com.thedndpub.data.dte.source.SourceRegistryFileDte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/** Immutable, case-insensitive source-code registry backed by packaged resources. */
@Component
public final class SourceRegistry {
    private static final String RESOURCE_PATH = "data/sources/registry.json";

    private final Map<String, SourceRecord> recordsByCode;
    private final Map<String, SourceRecord> recordsByAlias;

    @Autowired
    public SourceRegistry(ObjectMapper objectMapper, PublicationCatalog publicationCatalog) {
        this(load(objectMapper), publicationCatalog);
    }

    public SourceRegistry(SourceRegistryFileDte source, PublicationCatalog publicationCatalog) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(publicationCatalog, "publicationCatalog");

        Map<String, SourceRecord> codeIndex = new TreeMap<>();
        for (var sourceCode : source.sources()) {
            SourceIdentity identity = new SourceIdentity(sourceCode.code());
            PublicationDefinition publication = publicationCatalog.findBySourceCode(identity.code());
            SourceRecord record = new SourceRecord(
                    identity,
                    null,
                    publication,
                    publication == null
                            ? PublicationMetadataStatus.UNAVAILABLE
                            : PublicationMetadataStatus.AVAILABLE);
            String key = normalize(identity.code());
            if (codeIndex.putIfAbsent(key, record) != null) {
                throw new IllegalStateException("Duplicate source code ignoring case: " + identity.code());
            }
        }

        Map<String, SourceRecord> aliasIndex = new TreeMap<>();
        for (var sourceCode : source.sources()) {
            SourceRecord record = codeIndex.get(normalize(sourceCode.code()));
            for (String alias : sourceCode.aliases()) {
                SourceRecord identityMatch = codeIndex.get(normalize(alias));
                if (identityMatch != null && !identityMatch.identity().equals(record.identity())) {
                    throw new IllegalStateException("Source alias conflicts with source code '" + alias + "'");
                }
                putUniqueAlias(aliasIndex, alias, record);
            }
        }

        recordsByCode = Collections.unmodifiableMap(codeIndex);
        recordsByAlias = Collections.unmodifiableMap(aliasIndex);
    }

    public SourceRecord find(String code) {
        if (code == null) {
            return null;
        }
        String key = normalize(code);
        SourceRecord record = recordsByCode.get(key);
        return record == null ? recordsByAlias.get(key) : record;
    }

    public List<SourceRecord> records() {
        return List.copyOf(recordsByCode.values());
    }

    public int size() {
        return recordsByCode.size();
    }

    public long publicationCount() {
        return recordsByCode.values().stream().filter(record -> record.publication() != null).count();
    }

    private static void putUniqueAlias(Map<String, SourceRecord> aliases, String alias, SourceRecord record) {
        String key = normalize(alias);
        SourceRecord previous = aliases.putIfAbsent(key, record);
        if (previous != null && !previous.identity().equals(record.identity())) {
            throw new IllegalStateException("Conflicting source alias '" + alias + "'");
        }
    }

    private static SourceRegistryFileDte load(ObjectMapper objectMapper) {
        try (InputStream input = new ClassPathResource(RESOURCE_PATH).getInputStream()) {
            return objectMapper.readerFor(SourceRegistryFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load source registry resource " + RESOURCE_PATH, exception);
        }
    }

    private static String normalize(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Source code or alias must not be blank");
        }
        return code.trim().toLowerCase(Locale.ROOT);
    }
}
