package com.thedndpub.data.dto.magicvariant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MagicVariantDto(
        String id,
        String name,
        SourceReference source,
        String type,
        String edition,
        MagicVariantKind kind,
        List<MagicVariantRequirementDto> requires,
        MagicVariantRequirementDto excludes,
        Boolean ammunition,
        MagicVariantBaseItemReferenceDto baseItem,
        MagicVariantTransformationDto transformation,
        List<SourceReference> referenceSources,
        List<SourceReference> secondarySources,
        List<MagicVariantReprintDto> reprintedAs,
        Integer eligibleItemCount) {
    public MagicVariantDto {
        requires = List.copyOf(requires);
        referenceSources = List.copyOf(referenceSources);
        secondarySources = List.copyOf(secondarySources);
        reprintedAs = List.copyOf(reprintedAs);
    }
}
