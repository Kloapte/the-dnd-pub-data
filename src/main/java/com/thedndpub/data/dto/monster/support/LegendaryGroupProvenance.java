package com.thedndpub.data.dto.monster.support;

import com.fasterxml.jackson.core.JsonPointer;
import com.thedndpub.data.mechanics.syntax.SyntaxSourceOwner;

import java.util.List;
import java.util.Objects;

public record LegendaryGroupProvenance(
        SyntaxSourceOwner declaringOwner,
        String declarationPointer,
        List<LegendaryGroupId> copyLineage,
        LegendaryGroupId versionParent,
        List<LegendaryGroupId> templates) {
    public LegendaryGroupProvenance {
        Objects.requireNonNull(declaringOwner, "declaringOwner");
        JsonPointer.compile(Objects.requireNonNull(declarationPointer, "declarationPointer"));
        copyLineage = List.copyOf(copyLineage);
        templates = List.copyOf(templates);
    }
}
