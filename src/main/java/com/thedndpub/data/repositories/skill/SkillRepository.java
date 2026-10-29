package com.thedndpub.data.repositories.skill;

import com.thedndpub.data.dto.skill.SkillRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class SkillRepository {
    private final List<SkillRecord> skills;

    public SkillRepository(
            ObjectMapper objectMapper,
            @Value("${dndpub.skills.dataset:classpath:normalized/core-rules/skills.json}") Resource dataset) {
        this.skills = loadSkills(objectMapper, dataset);
    }

    public List<SkillRecord> findAll() {
        return skills;
    }

    private static List<SkillRecord> loadSkills(ObjectMapper objectMapper, Resource dataset) {
        try (InputStream inputStream = dataset.getInputStream()) {
            SkillRecord[] parsed = objectMapper.readValue(inputStream, SkillRecord[].class);
            if (parsed == null || parsed.length == 0) {
                throw new IllegalArgumentException("The Skill dataset must contain at least one Skill");
            }
            List<SkillRecord> skills = List.of(parsed);
            validateUniqueIds(skills);
            return skills;
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to load Skill dataset from " + dataset.getDescription(), exception);
        }
    }

    private static void validateUniqueIds(List<SkillRecord> skills) {
        Set<String> ids = new HashSet<>();
        for (SkillRecord skill : skills) {
            if (!ids.add(skill.id())) throw new IllegalStateException("Duplicate Skill id '" + skill.id() + "'");
        }
    }
}
