package com.thedndpub.data.dto.skill;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SkillSummaryDto(
        SkillId id,
        String name,
        SourceReference source,
        SkillAbility ability) {
    public static SkillSummaryDto from(SkillDefinition definition) {
        return new SkillSummaryDto(definition.id(), definition.name(), definition.source(), definition.ability());
    }
}
