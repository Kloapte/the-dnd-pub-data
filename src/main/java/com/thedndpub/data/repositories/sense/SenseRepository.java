package com.thedndpub.data.repositories.sense;

import com.thedndpub.data.dto.sense.SenseRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class SenseRepository {
    private final List<SenseRecord> senses;

    public SenseRepository(
            ObjectMapper objectMapper,
            @Value("${dndpub.senses.dataset:classpath:normalized/core-rules/senses.json}") Resource dataset) {
        this.senses = loadSenses(objectMapper, dataset);
    }

    public List<SenseRecord> findAll() {
        return senses;
    }

    private static List<SenseRecord> loadSenses(ObjectMapper objectMapper, Resource dataset) {
        try (InputStream inputStream = dataset.getInputStream()) {
            SenseRecord[] parsed = objectMapper.readValue(inputStream, SenseRecord[].class);
            if (parsed == null || parsed.length == 0) {
                throw new IllegalArgumentException("The Sense dataset must contain at least one Sense");
            }
            List<SenseRecord> senses = List.of(parsed);
            validateUniqueIds(senses);
            return senses;
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to load Sense dataset from " + dataset.getDescription(), exception);
        }
    }

    private static void validateUniqueIds(List<SenseRecord> senses) {
        Set<String> ids = new HashSet<>();
        for (SenseRecord sense : senses) {
            if (!ids.add(sense.id())) throw new IllegalStateException("Duplicate Sense id '" + sense.id() + "'");
        }
    }
}
