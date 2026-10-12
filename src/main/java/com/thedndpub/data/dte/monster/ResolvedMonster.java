package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public record ResolvedMonster(MonsterIdentity identity, MonsterSourceLocation location,
                              MonsterIdentity copyParent, List<MonsterIdentity> appliedTemplates,
                              ObjectNode content, ResolutionProvenance provenance) {
    public ResolvedMonster {
        appliedTemplates = List.copyOf(appliedTemplates);
        content = content.deepCopy();
    }

    @Override
    public ObjectNode content() {
        return content.deepCopy();
    }
}
