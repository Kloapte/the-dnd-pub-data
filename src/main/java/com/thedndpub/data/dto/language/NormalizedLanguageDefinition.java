package com.thedndpub.data.dto.language;

import com.thedndpub.data.dto.source.SourceIdentity;

import java.util.List;

/** Internal normalized definition; source presentation data is never projected automatically. */
public record NormalizedLanguageDefinition(
        LanguageId id,
        String name,
        SourceIdentity source,
        String type,
        String script,
        List<String> dialects) {
    public NormalizedLanguageDefinition {
        dialects = List.copyOf(dialects);
    }
}
