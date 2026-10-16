package com.thedndpub.data.dto.magicvariant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MagicVariantSummaryDto(
        String id,
        String name,
        SourceReference source,
        String type,
        MagicVariantKind kind,
        Integer eligibleItemCount) {
}
