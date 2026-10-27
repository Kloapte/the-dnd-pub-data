package com.thedndpub.data.repositories.condition;

import com.thedndpub.data.dto.condition.ConditionRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class ConditionRepository {
    private final List<ConditionRecord> conditions;

    public ConditionRepository(
            ObjectMapper objectMapper,
            @Value("${dndpub.conditions.dataset:classpath:normalized/core-rules/conditions.json}") Resource dataset) {
        this.conditions = loadConditions(objectMapper, dataset);
    }

    public List<ConditionRecord> findAll() {
        return conditions;
    }

    private static List<ConditionRecord> loadConditions(ObjectMapper objectMapper, Resource dataset) {
        try (InputStream inputStream = dataset.getInputStream()) {
            ConditionRecord[] parsed = objectMapper.readValue(inputStream, ConditionRecord[].class);
            if (parsed == null || parsed.length == 0) {
                throw new IllegalArgumentException("The Condition dataset must contain at least one Condition");
            }
            List<ConditionRecord> conditions = List.of(parsed);
            validateUniqueIds(conditions);
            return conditions;
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to load Condition dataset from " + dataset.getDescription(),
                    exception);
        }
    }

    private static void validateUniqueIds(List<ConditionRecord> conditions) {
        Set<String> ids = new HashSet<>();
        for (ConditionRecord condition : conditions) {
            if (!ids.add(condition.id())) {
                throw new IllegalStateException("Duplicate Condition id '" + condition.id() + "'");
            }
        }
    }
}
