package com.thedndpub.data.dto.reward;

import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.reward.RewardAdditionalSpellDte;
import com.thedndpub.data.dte.reward.RewardDte;
import com.thedndpub.data.dte.reward.RewardSpellAbilityChoiceDte;
import com.thedndpub.data.dte.reward.RewardSpellAbilityCodeDte;
import com.thedndpub.data.dte.reward.RewardSpellFrequencyDte;
import com.thedndpub.data.dte.reward.RewardSpellIdsDte;
import com.thedndpub.data.dte.reward.RewardsFileDte;
import org.springframework.stereotype.Component;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public final class RewardMapper {
    public RewardDefinition map(RewardDte source) {
        require(source.name(), "name");
        require(source.source(), "source");
        require(source.type(), "type");
        if (source.page() != null && source.page() < 1) {
            throw new IllegalArgumentException("Reward page must be positive");
        }
        return new RewardDefinition(
                new RewardId("reward:" + source.source() + ":" + slug(source.name())),
                source.name(), new SourceReference(new SourceIdentity(source.source()), source.page()), source.type(),
                source.rarity(), source.additionalSpells() == null ? List.of()
                        : source.additionalSpells().stream().map(RewardMapper::spellBenefit).toList(),
                source.seeAlsoFacility(), source.reprintedAs(),
                source.otherSources(), source.entries());
    }

    public RewardDetailDto detail(RewardDefinition definition) {
        return new RewardDetailDto(
                definition.id(), definition.name(), definition.source(), definition.type(), definition.rarity(),
                definition.additionalSpells().stream().map(RewardMapper::spellGrant).toList(), List.of());
    }

    private static RewardSpellBenefit spellBenefit(RewardAdditionalSpellDte spell) {
        String ability = spell.ability() instanceof RewardSpellAbilityCodeDte code ? code.code() : null;
        List<String> choices = spell.ability() instanceof RewardSpellAbilityChoiceDte choice
                ? choice.choose()
                : List.of();
        List<String> known = spell.known() == null ? List.of() : spell.known().spellIds();
        List<String> innate = List.of();
        Map<String, List<String>> daily = Map.of();
        Map<String, List<String>> limited = Map.of();
        Map<String, List<String>> rest = Map.of();
        List<String> ritual = List.of();
        if (spell.innate() instanceof RewardSpellIdsDte ids) {
            innate = ids.spellIds();
        } else if (spell.innate() instanceof RewardSpellFrequencyDte frequency) {
            daily = frequency.daily();
            limited = frequency.limited();
            rest = frequency.rest();
            ritual = frequency.ritual();
        }
        return new RewardSpellBenefit(ability, choices, known, innate, daily, limited, rest, ritual,
                spell.resourceName());
    }

    private static RewardSpellGrantDto spellGrant(RewardSpellBenefit spell) {
        return new RewardSpellGrantDto(spell.ability(), spell.abilityChoices(), spell.knownSpellIds(),
                spell.innateSpellIds(), spell.daily(), spell.limited(), spell.rest(), spell.ritualSpellIds(),
                spell.resourceName());
    }

    private static String slug(String value) {
        String slug = Normalizer.normalize(value, Normalizer.Form.NFKD).replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        if (slug.isEmpty()) {
            throw new IllegalArgumentException("Reward name cannot produce a stable ID: " + value);
        }
        return slug;
    }

    private static void require(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Reward " + field + " must not be blank");
        }
    }
}
