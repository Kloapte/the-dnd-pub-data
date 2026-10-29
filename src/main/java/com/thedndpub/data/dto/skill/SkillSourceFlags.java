package com.thedndpub.data.dto.skill;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SkillSourceFlags(Boolean srd, Boolean srd52, Boolean basicRules, Boolean basicRules2024) {
    public SkillSourceFlags {
        if (srd == null && srd52 == null && basicRules == null && basicRules2024 == null) {
            throw new IllegalArgumentException("Skill source flags require at least one flag");
        }
    }
}
