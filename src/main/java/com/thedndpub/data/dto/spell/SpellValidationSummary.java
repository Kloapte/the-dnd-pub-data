package com.thedndpub.data.dto.spell;

public record SpellValidationSummary(
        int sourceFileCount,
        int normalizedDefinitionCount,
        int validatedSourceIdentityCount,
        int resolvedReprintReferenceCount) {
}
