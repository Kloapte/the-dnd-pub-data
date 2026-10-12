package com.thedndpub.data.dte.monster;

import java.util.List;

public record MonsterSupplementalDte(List<String> environment, List<MonsterGearDte> gear,
                                     List<String> attachedItems, List<String> treasure, Boolean familiar,
                                     Boolean tokenCustom,
                                     Boolean hasToken, Double foundryTokenScale, String pbNote, String sizeNote,
                                     List<MonsterArtDte> altArt,
                                     String tokenCredit, Boolean srd, Boolean srd52, Boolean basicRules,
                                     Boolean basicRules2024,
                                     Boolean isReprinted) {
}
