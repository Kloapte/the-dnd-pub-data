package com.thedndpub.data.dte.monster;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;
import java.util.Map;

public record MonsterSpellcastingDte(String name, String type, String ability, String displayAs,
                                     List<EntryDte> headerEntries, List<EntryDte> footerEntries,
                                     List<MonsterSpellReferenceDte> will,
                                     Map<String, MonsterSpellLevelDte> spells,
                                     Map<String, List<MonsterSpellReferenceDte>> daily,
                                     Map<String, List<MonsterSpellReferenceDte>> rest,
                                     Map<String, List<MonsterSpellReferenceDte>> restLong,
                                     Map<String, List<MonsterSpellReferenceDte>> recharge,
                                     Map<String, List<MonsterSpellReferenceDte>> legendary,
                                     Map<String, List<MonsterSpellReferenceDte>> charges,
                                     String chargesItem, List<String> hidden, List<MonsterSpellReferenceDte> ritual) {
}
