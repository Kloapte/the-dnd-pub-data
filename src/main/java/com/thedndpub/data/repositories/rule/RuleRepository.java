package com.thedndpub.data.repositories.rule;

import com.thedndpub.data.dto.rule.RuleRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class RuleRepository {
    private final List<RuleRecord> rules;

    public RuleRepository(
            ObjectMapper objectMapper,
            @Value("${dndpub.rules.dataset:classpath:normalized/core-rules/rules.json}") Resource dataset) {
        this.rules = loadRules(objectMapper, dataset);
    }

    public List<RuleRecord> findAll() {
        return rules;
    }

    private static List<RuleRecord> loadRules(ObjectMapper objectMapper, Resource dataset) {
        try (InputStream inputStream = dataset.getInputStream()) {
            RuleRecord[] parsed = objectMapper.readValue(inputStream, RuleRecord[].class);
            if (parsed == null || parsed.length == 0) {
                throw new IllegalArgumentException("The Rule dataset must contain at least one Rule");
            }
            List<RuleRecord> rules = List.of(parsed);
            validateUniqueIds(rules);
            return rules;
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to load Rule dataset from " + dataset.getDescription(),
                    exception);
        }
    }

    private static void validateUniqueIds(List<RuleRecord> rules) {
        Set<String> ids = new HashSet<>();
        for (RuleRecord rule : rules) {
            if (!ids.add(rule.id())) {
                throw new IllegalStateException("Duplicate Rule id '" + rule.id() + "'");
            }
        }
    }
}
