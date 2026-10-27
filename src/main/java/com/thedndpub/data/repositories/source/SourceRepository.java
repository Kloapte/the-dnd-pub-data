package com.thedndpub.data.repositories.source;

import com.thedndpub.data.dto.source.Source;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class SourceRepository {
    private final List<Source> sources;

    public SourceRepository(
            ObjectMapper objectMapper,
            @Value("${dndpub.sources.dataset:classpath:normalized/sources/sources.json}") Resource dataset) {
        this.sources = loadSources(objectMapper, dataset);
    }

    public List<Source> findAll() {
        return sources;
    }

    private static List<Source> loadSources(ObjectMapper objectMapper, Resource dataset) {
        try (InputStream inputStream = dataset.getInputStream()) {
            Source[] parsed = objectMapper.readValue(inputStream, Source[].class);
            if (parsed == null || parsed.length == 0) {
                throw new IllegalArgumentException("The Source dataset must contain at least one Source");
            }
            List<Source> sources = List.of(parsed);
            validateUniqueCodes(sources);
            return sources;
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to load Source dataset from " + dataset.getDescription(),
                    exception);
        }
    }

    private static void validateUniqueCodes(List<Source> sources) {
        Set<String> codes = new HashSet<>();
        for (Source source : sources) {
            if (!codes.add(source.code())) {
                throw new IllegalStateException("Duplicate Source code '" + source.code() + "'");
            }
        }
    }
}
