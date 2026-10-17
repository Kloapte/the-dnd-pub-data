package com.thedndpub.data.dto.monster.support;

import com.thedndpub.data.dte.legendarygroup.LegendaryGroupTemplateDte;
import com.thedndpub.data.mechanics.syntax.SyntaxSourceOwner;
import com.thedndpub.data.mechanics.syntax.SyntaxText;

import java.util.List;
import java.util.Objects;

public record LegendaryGroupAuthoredTemplate(
        LegendaryGroupTemplateDte declaration,
        SyntaxSourceOwner owner,
        List<SyntaxText> syntaxTexts) {
    public LegendaryGroupAuthoredTemplate {
        Objects.requireNonNull(declaration, "declaration");
        Objects.requireNonNull(owner, "owner");
        syntaxTexts = List.copyOf(syntaxTexts);
    }

    public LegendaryGroupId id() {
        return LegendaryGroupId.from(declaration.name(), declaration.source());
    }
}
