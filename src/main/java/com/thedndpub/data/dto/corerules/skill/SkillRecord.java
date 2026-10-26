package com.thedndpub.data.dto.corerules.skill;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.corerules.AbilityScore;
import com.thedndpub.data.dto.corerules.CanonicalReference;
import com.thedndpub.data.dto.corerules.CanonicalSourceMetadata;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SkillRecord(
        String id,
        String name,
        CanonicalSourceMetadata sourceMetadata,
        AbilityScore ability,
        List<CanonicalReference> checkReferences) {
    public SkillRecord {
        if (id == null || !id.startsWith("skill:") || name == null || name.isBlank()
                || sourceMetadata == null || ability == null) {
            throw new IllegalArgumentException("Skill record requires identity, provenance, and ability");
        }
        checkReferences = checkReferences == null ? List.of() : List.copyOf(checkReferences);
    }
}
