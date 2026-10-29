package com.thedndpub.data.repositories.status;

import com.thedndpub.data.dto.status.StatusRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class StatusRepository {
    private final List<StatusRecord> statuses;

    public StatusRepository(
            ObjectMapper objectMapper,
            @Value("${dndpub.statuses.dataset:classpath:normalized/core-rules/statuses.json}") Resource dataset) {
        this.statuses = loadStatuses(objectMapper, dataset);
    }

    public List<StatusRecord> findAll() {
        return statuses;
    }

    private static List<StatusRecord> loadStatuses(ObjectMapper objectMapper, Resource dataset) {
        try (InputStream inputStream = dataset.getInputStream()) {
            StatusRecord[] parsed = objectMapper.readValue(inputStream, StatusRecord[].class);
            if (parsed == null || parsed.length == 0) {
                throw new IllegalArgumentException("The Status dataset must contain at least one Status");
            }
            List<StatusRecord> statuses = List.of(parsed);
            validateUniqueIds(statuses);
            return statuses;
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to load Status dataset from " + dataset.getDescription(), exception);
        }
    }

    private static void validateUniqueIds(List<StatusRecord> statuses) {
        Set<String> ids = new HashSet<>();
        for (StatusRecord status : statuses) {
            if (!ids.add(status.id())) throw new IllegalStateException("Duplicate Status id '" + status.id() + "'");
        }
    }
}
