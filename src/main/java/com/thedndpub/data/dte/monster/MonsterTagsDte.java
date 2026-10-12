package com.thedndpub.data.dte.monster;

import java.util.List;

public record MonsterTagsDte(List<String> actionTags, List<String> damageTags, List<String> damageTagsLegendary,
                             List<String> damageTagsSpell, List<String> languageTags, List<String> miscTags,
                             List<String> senseTags,
                             List<String> spellcastingTags, List<String> traitTags, List<String> conditionInflict,
                             List<String> conditionInflictLegendary, List<String> conditionInflictSpell) {
}
