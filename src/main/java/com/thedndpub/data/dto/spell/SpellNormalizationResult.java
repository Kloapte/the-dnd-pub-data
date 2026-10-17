package com.thedndpub.data.dto.spell;

import java.util.List;

public record SpellNormalizationResult(
        List<SpellDefinition> definitions,
        SpellValidationSummary validation) {
    public SpellNormalizationResult {
        definitions = List.copyOf(definitions);
    }
}
