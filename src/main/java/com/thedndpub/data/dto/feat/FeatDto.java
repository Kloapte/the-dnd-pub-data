package com.thedndpub.data.dto.feat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FeatDto(
        FeatId id,
        String name,
        SourceReference source,
        String category,
        List<FeatPrerequisiteDto> prerequisites,
        List<FeatAbilityIncreaseDto> abilityIncreases,
        List<FeatReferenceDto> reprintedAs,
        Boolean repeatable,
        List<FeatAdditionalSpellDto> additionalSpells) {
    public FeatDto {
        prerequisites = List.copyOf(prerequisites);
        abilityIncreases = List.copyOf(abilityIncreases);
        reprintedAs = List.copyOf(reprintedAs);
        additionalSpells = List.copyOf(additionalSpells);
    }

    public static FeatDto from(FeatDefinition feat) {
        return new FeatDto(feat.id(), feat.name(), feat.source(), feat.category(),
                feat.prerequisites(), feat.abilityIncreases(), feat.reprintedAs(), feat.repeatable(),
                feat.additionalSpells().stream().map(FeatDto::spellDto).toList());
    }

    private static FeatAdditionalSpellDto spellDto(FeatAdditionalSpell spell) {
        FeatSpellAbilityDto ability = spell.ability() == null && spell.abilityChoices().isEmpty()
                ? null
                : new FeatSpellAbilityDto(spell.ability(), spell.abilityChoices());
        return new FeatAdditionalSpellDto(spell.name(), ability, spellDto(spell.prepared()), spellDto(spell.known()),
                spellDto(spell.innate()), spellDto(spell.expanded()));
    }

    private static FeatSpellGrantNodeDto spellDto(FeatSpellGrantNode node) {
        if (node instanceof FeatSpellTextNode text) {
            return new FeatSpellTextDto(text.value());
        }
        if (node instanceof FeatSpellCountNode count) {
            return new FeatSpellCountDto(count.value());
        }
        if (node instanceof FeatSpellFlagNode flag) {
            return new FeatSpellFlagDto(flag.value());
        }
        if (node instanceof FeatSpellArrayNode array) {
            return new FeatSpellArrayDto(array.values().stream().map(FeatDto::spellDto).toList());
        }
        if (node instanceof FeatSpellObjectNode object) {
            Map<String, FeatSpellGrantNodeDto> values = new TreeMap<>();
            object.values().forEach((key, value) -> values.put(key, spellDto(value)));
            return new FeatSpellObjectDto(values);
        }
        return null;
    }
}
