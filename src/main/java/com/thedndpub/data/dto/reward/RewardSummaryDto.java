package com.thedndpub.data.dto.reward;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceIdentity;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RewardSummaryDto(
        RewardId id,
        String name,
        SourceIdentity source,
        Integer page,
        String type,
        String rarity) {
    public static RewardSummaryDto from(RewardDefinition definition) {
        return new RewardSummaryDto(definition.id(), definition.name(), definition.source().identity(),
                definition.source().page(), definition.type(), definition.rarity());
    }
}
