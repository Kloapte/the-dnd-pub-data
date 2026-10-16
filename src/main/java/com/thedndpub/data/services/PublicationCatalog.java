package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.book.PublicationDefinition;
import com.thedndpub.data.dto.book.PublicationImportProvenance;
import com.thedndpub.data.dto.book.PublicationImportRecord;
import com.thedndpub.data.dto.book.PublicationMapper;
import com.thedndpub.data.dto.book.PublicationSummaryDto;
import com.thedndpub.data.dte.book.BookDte;
import com.thedndpub.data.dte.book.BooksFileDte;
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
import java.util.TreeMap;

@Component
public final class PublicationCatalog {
    private static final String RESOURCE_PATH = "data/books.json";

    private final Map<String, PublicationDefinition> publicationsById;
    private final Map<String, PublicationDefinition> publicationsBySourceCode;
    private final Map<String, PublicationImportProvenance> provenanceById;
    private final List<PublicationSummaryDto> summaries;

    @Autowired
    public PublicationCatalog(ObjectMapper objectMapper, PublicationMapper mapper) {
        this(load(objectMapper), mapper);
    }

    public PublicationCatalog(BooksFileDte source, PublicationMapper mapper) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(mapper, "mapper");
        if (source.book().isEmpty()) {
            throw new IllegalStateException("Book source contains no publications");
        }

        Map<String, PublicationDefinition> idIndex = new TreeMap<>();
        Map<String, PublicationDefinition> codeIndex = new TreeMap<>();
        Map<String, PublicationImportProvenance> provenanceIndex = new TreeMap<>();
        List<PublicationSummaryDto> summaryList = new ArrayList<>(source.book().size());
        for (BookDte book : source.book()) {
            PublicationImportRecord imported = mapper.map(book);
            PublicationDefinition publication = imported.definition();
            String canonicalId = publication.id().value();
            if (idIndex.putIfAbsent(canonicalId, publication) != null) {
                throw new IllegalStateException("Duplicate publication ID '" + canonicalId + "'");
            }
            putUnique(codeIndex, canonicalId, publication, "publication source code");
            putUnique(codeIndex, imported.provenance().sourceId(), publication, "publication source alias");
            provenanceIndex.put(canonicalId, imported.provenance());
            summaryList.add(PublicationSummaryDto.from(publication));
        }

        summaryList.sort(Comparator
                .comparing(PublicationSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.id().value()));
        publicationsById = Collections.unmodifiableMap(idIndex);
        publicationsBySourceCode = Collections.unmodifiableMap(codeIndex);
        provenanceById = Collections.unmodifiableMap(provenanceIndex);
        summaries = List.copyOf(summaryList);
    }

    public List<PublicationSummaryDto> summaries() {
        return summaries;
    }

    public PublicationDefinition findById(String id) {
        return publicationsById.get(id);
    }

    public PublicationDefinition findBySourceCode(String sourceCode) {
        return sourceCode == null ? null : publicationsBySourceCode.get(sourceCode);
    }

    public PublicationImportProvenance provenanceById(String id) {
        return provenanceById.get(id);
    }

    public List<PublicationDefinition> findByName(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        String normalizedName = normalize(name);
        return publicationsById.values().stream()
                .filter(publication -> publication.name().equalsIgnoreCase(normalizedName)
                        || publication.aliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(normalizedName)))
                .sorted(Comparator.comparing(PublicationDefinition::name, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(publication -> publication.id().value()))
                .toList();
    }

    public int size() {
        return publicationsById.size();
    }

    private static BooksFileDte load(ObjectMapper objectMapper) {
        try (InputStream input = new ClassPathResource(RESOURCE_PATH).getInputStream()) {
            return objectMapper.readerFor(BooksFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load book resource " + RESOURCE_PATH, exception);
        }
    }

    private static void putUnique(
            Map<String, PublicationDefinition> index,
            String key,
            PublicationDefinition value,
            String kind) {
        PublicationDefinition existing = index.putIfAbsent(key, value);
        if (existing != null && !existing.id().equals(value.id())) {
            throw new IllegalStateException("Conflicting " + kind + " '" + key + "'");
        }
    }

    private static String normalize(String name) {
        return name.trim().toLowerCase(Locale.ROOT);
    }
}
