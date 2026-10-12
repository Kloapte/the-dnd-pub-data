package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;
import java.util.Map;

@JsonDeserialize(using = MonsterCopyModifierDeserializer.class)
public record MonsterCopyModifierDte(String mode, String replace, String with, String flags,
                                     MonsterNamesDte names, MonsterCopyModifierPayloadDte items,
                                     MonsterFeatureRenameDte renames, Integer index, List<String> props,
                                     String prop, Map<String, MonsterSkillValueDte> skills, Boolean value,
                                     Map<String, List<MonsterSpellModificationDte>> daily,
                                     Map<String, List<MonsterSpellModificationDte>> yearly,
                                     Map<String, MonsterSpellLevelModifierDte> spells,
                                     List<MonsterSpellModificationDte> will) {
}
