package com.thedndpub.data.dto.monster.internal;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * Source-provided monster tags retained for backend filtering, not exposed in public responses.
 */
public record MonsterClassification(Set<String> actionTags, Set<String> damageTags,
                                    Set<String> legendaryDamageTags, Set<String> spellDamageTags,
                                    Set<String> conditionInflict, Set<String> legendaryConditionInflict,
                                    Set<String> spellConditionInflict, Set<String> languageTags,
                                    Set<String> miscTags, Set<String> senseTags,
                                    Set<String> spellcastingTags, Set<String> traitTags) {
    public MonsterClassification {
        actionTags = immutableSortedSet(actionTags);
        damageTags = immutableSortedSet(damageTags);
        legendaryDamageTags = immutableSortedSet(legendaryDamageTags);
        spellDamageTags = immutableSortedSet(spellDamageTags);
        conditionInflict = immutableSortedSet(conditionInflict);
        legendaryConditionInflict = immutableSortedSet(legendaryConditionInflict);
        spellConditionInflict = immutableSortedSet(spellConditionInflict);
        languageTags = immutableSortedSet(languageTags);
        miscTags = immutableSortedSet(miscTags);
        senseTags = immutableSortedSet(senseTags);
        spellcastingTags = immutableSortedSet(spellcastingTags);
        traitTags = immutableSortedSet(traitTags);
    }

    private static Set<String> immutableSortedSet(Set<String> values) {
        return Collections.unmodifiableSet(new TreeSet<>(values));
    }
}
