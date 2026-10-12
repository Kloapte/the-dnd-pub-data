package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Set;

public record ResolutionProvenance(String resolverRevision, String upstreamRevision, String originalCopyName,
                                   String originalCopySource, boolean versionsPreserved,
                                   Set<String> appliedModifierModes,
                                   List<JsonNode> sourceVersionDefinitions,
                                   MonsterVersionProvenance generatedVersion) {
    public ResolutionProvenance {
        appliedModifierModes = Set.copyOf(appliedModifierModes);
        sourceVersionDefinitions = sourceVersionDefinitions == null ? List.<JsonNode>of()
                : sourceVersionDefinitions.stream().map(node -> (JsonNode) node.deepCopy()).toList();
    }

    public ResolutionProvenance(String resolverRevision, String upstreamRevision, String originalCopyName,
                                String originalCopySource, boolean versionsPreserved, Set<String> appliedModifierModes) {
        this(resolverRevision, upstreamRevision, originalCopyName, originalCopySource, versionsPreserved,
                appliedModifierModes, List.of(), null);
    }

    @Override
    public List<JsonNode> sourceVersionDefinitions() {
        return sourceVersionDefinitions.stream().map(node -> (JsonNode) node.deepCopy()).toList();
    }

    public ResolutionProvenance withSourceVersions(List<JsonNode> definitions) {
        return new ResolutionProvenance(resolverRevision, upstreamRevision, originalCopyName, originalCopySource,
                versionsPreserved, appliedModifierModes, definitions, generatedVersion);
    }

    public ResolutionProvenance forGeneratedVersion(Set<String> modes, MonsterVersionProvenance version) {
        return new ResolutionProvenance(resolverRevision, upstreamRevision, originalCopyName, originalCopySource,
                false, modes, List.<JsonNode>of(), version);
    }
}
