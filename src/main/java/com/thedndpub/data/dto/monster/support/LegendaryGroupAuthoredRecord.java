package com.thedndpub.data.dto.monster.support;

import com.thedndpub.data.dte.legendarygroup.LegendaryGroupSourceDte;
import com.thedndpub.data.mechanics.syntax.SyntaxSourceOwner;
import com.thedndpub.data.mechanics.syntax.SyntaxText;

import java.util.List;
import java.util.Objects;

public record LegendaryGroupAuthoredRecord(
        LegendaryGroupSourceDte declaration,
        SyntaxSourceOwner owner,
        List<SyntaxText> syntaxTexts) {
    public LegendaryGroupAuthoredRecord {
        Objects.requireNonNull(declaration, "declaration");
        Objects.requireNonNull(owner, "owner");
        syntaxTexts = List.copyOf(syntaxTexts);
    }

    public LegendaryGroupId id() {
        return LegendaryGroupId.from(declaration.name(), declaration.source());
    }
}
