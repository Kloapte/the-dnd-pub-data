package com.thedndpub.data.repositories.action;

import com.thedndpub.data.dto.action.ActionRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class ActionRepository {
    private final List<ActionRecord> actions;

    public ActionRepository(
            ObjectMapper objectMapper,
            @Value("${dndpub.actions.dataset:classpath:normalized/core-rules/actions.json}") Resource dataset) {
        this.actions = loadActions(objectMapper, dataset);
    }

    public List<ActionRecord> findAll() {
        return actions;
    }

    private static List<ActionRecord> loadActions(ObjectMapper objectMapper, Resource dataset) {
        try (InputStream inputStream = dataset.getInputStream()) {
            ActionRecord[] parsed = objectMapper.readValue(inputStream, ActionRecord[].class);
            if (parsed == null || parsed.length == 0) {
                throw new IllegalArgumentException("The Action dataset must contain at least one Action");
            }
            List<ActionRecord> actions = List.of(parsed);
            validateUniqueIds(actions);
            return actions;
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to load Action dataset from " + dataset.getDescription(),
                    exception);
        }
    }

    private static void validateUniqueIds(List<ActionRecord> actions) {
        Set<String> ids = new HashSet<>();
        for (ActionRecord action : actions) {
            if (!ids.add(action.id())) {
                throw new IllegalStateException("Duplicate Action id '" + action.id() + "'");
            }
        }
    }
}
