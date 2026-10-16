package com.thedndpub.data.services;

import com.thedndpub.data.dto.skill.SkillDefinition;
import com.thedndpub.data.dto.skill.SkillDto;
import com.thedndpub.data.dto.skill.SkillSummaryDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public final class SkillsService {
    private final SkillCatalog catalog;

    public SkillsService(SkillCatalog catalog) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
    }

    public List<SkillSummaryDto> findSkills(String name, String source) {
        String normalizedName = normalize(name);
        String normalizedSource = normalize(source);
        return catalog.summaries().stream()
                .filter(skill -> normalizedName == null
                        || skill.name().toLowerCase(Locale.ROOT).contains(normalizedName))
                .filter(skill -> normalizedSource == null
                        || skill.source().identity().code().equalsIgnoreCase(normalizedSource))
                .toList();
    }

    public SkillDto findById(String id) {
        SkillDefinition definition = catalog.findById(id);
        if (definition == null) {
            throw new SkillNotFoundException("No skill found with ID '" + id + "'");
        }
        return SkillDto.from(definition);
    }

    public List<SkillDto> findByName(String name, String source) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Skill name is required");
        }

        List<SkillDto> matches = catalog.findByName(name).stream()
                .filter(definition -> source == null || source.isBlank()
                        || definition.source().identity().code().equalsIgnoreCase(source.trim()))
                .map(SkillDto::from)
                .toList();
        if (matches.isEmpty()) {
            String sourceDetail = source == null || source.isBlank() ? "" : " and source '" + source + "'";
            throw new SkillNotFoundException("No skill found with name '" + name + "'" + sourceDetail);
        }
        return matches;
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase(Locale.ROOT);
    }
}
