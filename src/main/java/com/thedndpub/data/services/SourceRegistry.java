package com.thedndpub.data.services;

import com.thedndpub.data.dto.source.PublicationReference;
import com.thedndpub.data.dto.source.Source;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class SourceRegistry {
    private static final String DATASET_CLASSPATH = "classpath:normalized/sources/sources.json";

    private final List<Source> sources;
    private final Map<String, Source> sourcesByCode;
    private final Map<String, Source> sourcesByAlias;

    public SourceRegistry(
            ObjectMapper objectMapper,
            @Value("${dndpub.sources.dataset:classpath:normalized/sources/sources.json}") Resource dataset) {
        List<Source> loadedSources = loadSources(objectMapper, dataset);
        RegistryIndexes indexes = buildIndexes(loadedSources, dataset.getDescription());
        this.sources = List.copyOf(loadedSources);
        this.sourcesByCode = Map.copyOf(indexes.sourcesByCode());
        this.sourcesByAlias = Map.copyOf(indexes.sourcesByAlias());
    }

    public List<Source> findAll() {
        return sources;
    }

    public Optional<Source> findByCode(String code) {
        return Optional.ofNullable(sourcesByCode.get(code));
    }

    public Optional<Source> findByCodeAlias(String alias) {
        return Optional.ofNullable(sourcesByAlias.get(alias));
    }

    public Optional<Source> findByCodeOrAlias(String codeOrAlias) {
        Source source = sourcesByCode.get(codeOrAlias);
        return Optional.ofNullable(source != null ? source : sourcesByAlias.get(codeOrAlias));
    }

    private static List<Source> loadSources(ObjectMapper objectMapper, Resource dataset) {
        try (InputStream inputStream = dataset.getInputStream()) {
            Source[] parsed = objectMapper.readValue(inputStream, Source[].class);
            if (parsed == null || parsed.length == 0) {
                throw new IllegalArgumentException("The Source dataset must contain at least one Source");
            }
            return List.of(parsed);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to load Source dataset from " + DATASET_CLASSPATH
                            + " (configured resource: " + dataset.getDescription() + ")",
                    exception);
        }
    }

    private static RegistryIndexes buildIndexes(List<Source> sources, String datasetDescription) {
        Map<String, Source> sourcesByCode = new LinkedHashMap<>();
        Map<String, Source> sourcesByAlias = new LinkedHashMap<>();
        Map<String, String> codeOwnersByFoldedValue = new LinkedHashMap<>();
        Map<String, String> aliasOwnersByFoldedValue = new LinkedHashMap<>();
        Set<String> publicationIdentityKeys = new HashSet<>();

        for (Source source : sources) {
            String foldedCode = fold(source.code());
            String previousCode = codeOwnersByFoldedValue.putIfAbsent(foldedCode, source.code());
            if (previousCode != null) {
                throw invalidDataset(datasetDescription,
                        "Duplicate or case-colliding Source codes '" + previousCode + "' and '" + source.code() + "'");
            }
            if (sourcesByCode.putIfAbsent(source.code(), source) != null) {
                throw invalidDataset(datasetDescription, "Duplicate Source code '" + source.code() + "'");
            }
        }

        for (Source source : sources) {
            for (String alias : source.codeAliases()) {
                String foldedAlias = fold(alias);
                String codeCollision = codeOwnersByFoldedValue.get(foldedAlias);
                if (codeCollision != null) {
                    throw invalidDataset(datasetDescription,
                            "Code alias '" + alias + "' collides with Source code '" + codeCollision + "'");
                }
                String previousAlias = aliasOwnersByFoldedValue.putIfAbsent(foldedAlias, alias);
                if (previousAlias != null) {
                    throw invalidDataset(datasetDescription, "Duplicate or case-colliding code alias '" + alias + "'");
                }
                sourcesByAlias.put(alias, source);
            }
        }

        for (Source source : sources) {
            for (PublicationReference publication : source.publications()) {
                String publicationId = publication.publicationId() == null
                        ? source.code()
                        : publication.publicationId();
                String identityKey = source.code() + "\u0000" + publication.kind() + "\u0000" + publicationId;
                if (!publicationIdentityKeys.add(identityKey)) {
                    throw invalidDataset(datasetDescription,
                            "Duplicate publication identity for Source '" + source.code()
                                    + "', kind '" + publication.kind() + "', ID '" + publicationId + "'");
                }
            }
        }

        return new RegistryIndexes(sourcesByCode, sourcesByAlias);
    }

    private static String fold(String value) {
        return value.toLowerCase(Locale.ROOT);
    }

    private static IllegalStateException invalidDataset(String datasetDescription, String reason) {
        return new IllegalStateException(
                "Invalid Source dataset at " + datasetDescription + ": " + reason);
    }

    private record RegistryIndexes(
            Map<String, Source> sourcesByCode,
            Map<String, Source> sourcesByAlias) {
    }
}
