package com.thedndpub.data.dto.charoption;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceIdentity;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CharacterOptionReprintDto(String name, SourceIdentity source, String targetKind) {
    public static CharacterOptionReprintDto from(CharacterOptionReprint reprint) {
        return new CharacterOptionReprintDto(reprint.name(), reprint.source(), reprint.targetKind());
    }
}
