package com.thedndpub.data.dto.corerules.language;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.corerules.CanonicalReference;
import com.thedndpub.data.dto.corerules.CanonicalSourceMetadata;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LanguageRecord(
        String id,
        String name,
        CanonicalSourceMetadata sourceMetadata,
        String category,
        String script,
        List<CanonicalReference> origin,
        List<LanguageSpeaker> typicalSpeakers,
        List<ConcealedMessage> communicationCapabilities,
        List<MutualIntelligibility> intelligibility,
        List<String> communicationModes,
        List<CanonicalReference> dialects,
        List<CanonicalReference> references) {
    public LanguageRecord {
        if (id == null || !id.startsWith("language:") || name == null || name.isBlank() || sourceMetadata == null) {
            throw new IllegalArgumentException("Language record requires identity and provenance");
        }
        origin = copyOrNull(origin);
        typicalSpeakers = copyOrNull(typicalSpeakers);
        communicationCapabilities = copyOrNull(communicationCapabilities);
        intelligibility = copyOrNull(intelligibility);
        communicationModes = copyOrNull(communicationModes);
        dialects = copyOrNull(dialects);
        references = copyOrNull(references);
    }

    private static <T> List<T> copyOrNull(List<T> values) {
        return values == null ? null : List.copyOf(values);
    }
}
