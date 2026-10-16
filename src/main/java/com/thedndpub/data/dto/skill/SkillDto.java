package com.thedndpub.data.dto.skill;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SkillDto(
        SkillId id,
        String name,
        SourceReference source,
        SkillAbility ability) {
    public static SkillDto from(SkillDefinition definition) {
        return new SkillDto(definition.id(), definition.name(), definition.source(), definition.ability());
    }
}
