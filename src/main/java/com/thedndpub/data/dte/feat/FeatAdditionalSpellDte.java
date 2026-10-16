package com.thedndpub.data.dte.feat;

public record FeatAdditionalSpellDte(
        FeatSpellAbilityDte ability,
        FeatSpellGrantNodeDte prepared,
        FeatSpellGrantNodeDte known,
        FeatSpellGrantNodeDte innate,
        FeatSpellGrantNodeDte expanded,
        String name) {
}
