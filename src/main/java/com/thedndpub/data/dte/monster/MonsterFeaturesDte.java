package com.thedndpub.data.dte.monster;

import java.util.List;

public record MonsterFeaturesDte(List<MonsterFeatureDte> trait, List<MonsterFeatureDte> action,
                                 List<MonsterFeatureDte> bonus, List<MonsterFeatureDte> reaction,
                                 List<MonsterFeatureDte> legendary,
                                 List<MonsterFeatureDte> mythic, List<MonsterFeatureDte> variant,
                                 List<MonsterSpellcastingDte> spellcasting,
                                 String actionNote, String reactionNote, List<String> legendaryHeader,
                                 List<String> mythicHeader,
                                 List<String> reactionHeader, Integer legendaryActions, Integer legendaryActionsLair) {
}
