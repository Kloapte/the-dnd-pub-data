package com.thedndpub.data.dto.language;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.language.enums.LanguageCategory;
import com.thedndpub.data.dto.language.enums.LanguageCommunicationMode;
import com.thedndpub.data.dto.language.communication.LanguageConcealedMessage;
import com.thedndpub.data.dto.language.communication.LanguageIntelligibility;
import com.thedndpub.data.dto.language.speakers.LanguageTypicalSpeaker;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record LanguageRecord(
        String id,
        String name,
        LanguageSourceMetadata sourceMetadata,
        LanguageCategory category,
        String script,
        List<LanguageReference> origin,
        List<LanguageTypicalSpeaker> typicalSpeakers,
        List<LanguageConcealedMessage> communicationCapabilities,
        List<LanguageIntelligibility> intelligibility,
        List<LanguageCommunicationMode> communicationModes,
        List<LanguageReference> dialects,
        List<LanguageReference> references) {

    public LanguageRecord {
        origin = origin == null ? List.of() : List.copyOf(origin);
        typicalSpeakers = typicalSpeakers == null ? List.of() : List.copyOf(typicalSpeakers);
        communicationCapabilities = communicationCapabilities == null
                ? List.of()
                : List.copyOf(communicationCapabilities);
        intelligibility = intelligibility == null ? List.of() : List.copyOf(intelligibility);
        communicationModes = communicationModes == null ? List.of() : List.copyOf(communicationModes);
        dialects = dialects == null ? List.of() : List.copyOf(dialects);
        references = references == null ? List.of() : List.copyOf(references);
    }
}
