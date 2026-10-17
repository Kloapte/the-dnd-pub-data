package com.thedndpub.data.dto.cultboon;

import com.thedndpub.data.dto.source.SourceIdentity;

import java.util.Objects;

public record CultBoonReprint(String name, SourceIdentity source, String tag, String authoredUid) {
    public CultBoonReprint {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(authoredUid, "authoredUid");
    }
}
