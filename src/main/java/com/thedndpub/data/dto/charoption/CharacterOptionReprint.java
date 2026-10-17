package com.thedndpub.data.dto.charoption;

import com.thedndpub.data.dto.source.SourceIdentity;

import java.util.Objects;

/** Explicit reprint target identity. The current resolver has no Background target adapter. */
public record CharacterOptionReprint(String name, SourceIdentity source, String targetKind, String authoredUid) {
    public CharacterOptionReprint {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(targetKind, "targetKind");
        Objects.requireNonNull(authoredUid, "authoredUid");
    }
}
