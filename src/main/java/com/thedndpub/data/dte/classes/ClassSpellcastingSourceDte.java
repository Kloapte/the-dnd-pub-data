package com.thedndpub.data.dte.classes;

import java.util.List;

/** Common authored spellcasting fields; absence is not a negative spellcasting rule. */
public sealed interface ClassSpellcastingSourceDte permits ClassDte, SubclassDte {
    String casterProgression();

    String spellcastingAbility();

    List<Integer> cantripProgression();

    List<Integer> spellsKnownProgression();

    List<Integer> preparedSpellsProgression();

    String preparedSpellsChange();

    List<ClassAdditionalSpellsDte> additionalSpells();
}
