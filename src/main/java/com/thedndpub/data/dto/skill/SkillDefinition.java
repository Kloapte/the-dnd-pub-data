package com.thedndpub.data.dto.skill;

import com.thedndpub.data.dto.source.SourceReference;

import java.util.Objects;

/** Normalized, source-specific skill definition without character state or source prose. */
public record SkillDefinition(
        SkillId id,
        String name,
        SourceReference source,
        SkillAbility ability) {
    public SkillDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(ability, "ability");
    }
}
