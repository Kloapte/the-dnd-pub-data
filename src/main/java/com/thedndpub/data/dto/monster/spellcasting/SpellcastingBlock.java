package com.thedndpub.data.dto.monster.spellcasting;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.monster.content.ContentValue;
import com.thedndpub.data.dto.monster.content.MonsterContentDto;
import java.util.List;
import java.util.Map;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SpellcastingBlock(String name, String type, String ability, String displayAs,
                                List<MonsterContentDto> header, List<MonsterContentDto> footer,
                                List<SpellReference> atWill, Map<String, SpellLevel> spellLevels,
                                Map<String, Map<String, List<SpellReference>>> limitedUses,
                                List<SpellReference> constant, List<SpellReference> rituals, Set<String> hidden,
                                String chargesItem,
                                Integer saveDc, Integer attackBonus, Integer casterLevel,
                                Map<String, Integer> slots, Map<String, ContentValue> sourceProperties) {
}
