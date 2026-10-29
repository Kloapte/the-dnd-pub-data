package com.thedndpub.data.dto.skill;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.skill.enums.SkillAbility;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SkillRecord(
        String id,
        String name,
        SkillSourceMetadata sourceMetadata,
        SkillAbility ability,
        List<SkillReference> checkReferences) {
    private static final Pattern ID = Pattern.compile(
            "^skill:[a-z0-9]+(?:-[a-z0-9]+)*:[a-z0-9]+(?:-[a-z0-9]+)*$");

    public SkillRecord {
        Objects.requireNonNull(id, "Skill id is required");
        if (!ID.matcher(id).matches()) throw new IllegalArgumentException("Invalid Skill id: " + id);
        Objects.requireNonNull(name, "Skill name is required");
        if (name.isEmpty()) throw new IllegalArgumentException("Skill name must not be empty");
        Objects.requireNonNull(sourceMetadata, "Skill sourceMetadata is required");
        Objects.requireNonNull(ability, "Skill ability is required");
        if (checkReferences != null && checkReferences.isEmpty()) {
            throw new IllegalArgumentException("Skill checkReferences must not be empty when present");
        }
        checkReferences = checkReferences == null ? List.of() : List.copyOf(checkReferences);
    }
}
