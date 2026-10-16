package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassSummaryDto(String id, String name, SourceReference source, Integer page,
        ClassHitDice hitDice, java.util.List<ClassAbilitySet> primaryAbilities, String spellcastingAbility,
        ClassSourceMetadataDto sources) {
    public ClassSummaryDto {
        primaryAbilities = primaryAbilities == null ? java.util.List.of() : java.util.List.copyOf(primaryAbilities);
    }
}
