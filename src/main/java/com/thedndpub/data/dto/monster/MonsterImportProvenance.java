package com.thedndpub.data.dto.monster;

import com.thedndpub.data.dto.monster.MonsterDto.MonsterId;
import com.thedndpub.data.dte.monster.MonsterIdentity;

import java.util.List;
import java.util.Map;

/**
 * Internal trace information kept beside, not inside, the public monster definition.
 */
public record MonsterImportProvenance(String sourceFile, int sourceArrayIndex,
                                      String canonicalName, String canonicalSource,
                                      MonsterId copyParentId, MonsterId versionParentId,
                                      Integer versionDefinitionIndex, Integer implementationIndex,
                                      List<MonsterId> appliedTemplateIds,
                                      Map<String, String> versionVariables) {
    public MonsterImportProvenance {
        appliedTemplateIds = List.copyOf(appliedTemplateIds);
        versionVariables = Map.copyOf(versionVariables);
    }
}
