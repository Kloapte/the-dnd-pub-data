package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Map;

/**
 * Source ancestry retained for one generated monster version.
 */
public record MonsterVersionProvenance(MonsterIdentity containingMonster,
                                       int versionDefinitionIndex,
                                       Integer implementationIndex,
                                       Map<String, String> variables,
                                       MonsterIdentity containingCopyParent,
                                       ObjectNode sourceDefinition) {
    public MonsterVersionProvenance {
        variables = variables == null ? Map.of() : Map.copyOf(variables);
        sourceDefinition = sourceDefinition.deepCopy();
    }

    @Override
    public ObjectNode sourceDefinition() {
        return sourceDefinition.deepCopy();
    }
}
