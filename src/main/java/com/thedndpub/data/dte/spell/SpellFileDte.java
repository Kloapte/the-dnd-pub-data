package com.thedndpub.data.dte.spell;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SpellFileDte(@JsonProperty("spell") List<SpellDte> spells) {
    public SpellFileDte {
        spells = List.copyOf(spells);
    }
}
