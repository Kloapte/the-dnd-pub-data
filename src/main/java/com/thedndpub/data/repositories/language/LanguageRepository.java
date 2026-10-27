package com.thedndpub.data.repositories.language;

import com.thedndpub.data.dto.language.LanguageRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class LanguageRepository {
    private final List<LanguageRecord> languages;

    public LanguageRepository(
            ObjectMapper objectMapper,
            @Value("${dndpub.languages.dataset:classpath:normalized/core-rules/languages.json}") Resource dataset) {
        this.languages = loadLanguages(objectMapper, dataset);
    }

    public List<LanguageRecord> findAll() {
        return languages;
    }

    private static List<LanguageRecord> loadLanguages(ObjectMapper objectMapper, Resource dataset) {
        try (InputStream inputStream = dataset.getInputStream()) {
            LanguageRecord[] parsed = objectMapper.readValue(inputStream, LanguageRecord[].class);
            if (parsed == null || parsed.length == 0) {
                throw new IllegalArgumentException("The Language dataset must contain at least one Language");
            }
            List<LanguageRecord> languages = List.of(parsed);
            validateUniqueIds(languages);
            return languages;
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to load Language dataset from " + dataset.getDescription(),
                    exception);
        }
    }

    private static void validateUniqueIds(List<LanguageRecord> languages) {
        Set<String> ids = new HashSet<>();
        for (LanguageRecord language : languages) {
            if (!ids.add(language.id())) {
                throw new IllegalStateException("Duplicate Language id '" + language.id() + "'");
            }
        }
    }
}
