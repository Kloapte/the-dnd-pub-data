package com.thedndpub.data.dto.skill;

import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.skill.SkillDte;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;

@Component
public final class SkillMapper {
    public SkillDefinition map(SkillDte source) {
        return mapRecord(source).definition();
    }

    public SkillImportRecord mapRecord(SkillDte source) {
        Objects.requireNonNull(source, "source");
        requireValue(source.name(), "name");
        requireValue(source.source(), "source");
        if (source.page() == null || source.page() < 1) {
            throw new IllegalArgumentException("Skill page must be a positive integer");
        }
        if (source.entries() == null || source.entries().isEmpty()) {
            throw new IllegalArgumentException("Skill entries are required and must not be empty");
        }

        SourceIdentity sourceIdentity = new SourceIdentity(source.source());
        SkillDefinition definition = new SkillDefinition(
                new SkillId("skill:" + slug(source.source()) + ":" + slug(source.name())),
                source.name(),
                new SourceReference(sourceIdentity, source.page()),
                SkillAbility.fromCode(source.ability()));
        SkillImportProvenance provenance = new SkillImportProvenance(
                "data/skills.json",
                source.entries(),
                source.reprintedAs(),
                source.srd(),
                source.basicRules(),
                source.srd52());
        return new SkillImportRecord(definition, provenance);
    }

    private static void requireValue(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Skill " + field + " is required");
        }
    }

    private static String slug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Cannot create a stable skill ID from '" + value + "'");
        }
        return normalized;
    }
}
