package com.thedndpub.data.dto.reward;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.references.PublicCrossCatalogReferenceDto;
import com.thedndpub.data.dto.source.SourceReference;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RewardDetailDto(
        RewardId id,
        String name,
        SourceReference source,
        String type,
        String rarity,
        List<RewardSpellGrantDto> spellGrants,
        List<PublicCrossCatalogReferenceDto> crossCatalogReferences) {
    public RewardDetailDto {
        spellGrants = spellGrants == null ? List.of() : List.copyOf(spellGrants);
        crossCatalogReferences = crossCatalogReferences == null ? List.of() : List.copyOf(crossCatalogReferences);
    }

    public RewardDetailDto withReferences(List<PublicCrossCatalogReferenceDto> references) {
        return new RewardDetailDto(id, name, source, type, rarity, spellGrants, references);
    }
}
