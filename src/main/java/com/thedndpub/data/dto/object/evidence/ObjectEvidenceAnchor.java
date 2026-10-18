package com.thedndpub.data.dto.object.evidence;

import com.thedndpub.data.mechanics.syntax.SyntaxSpan;
import com.thedndpub.data.mechanics.syntax.SyntaxTextLocation;

import java.util.Objects;

/** Original JSON node location; only syntax substrings have a UTF-16 span. */
public record ObjectEvidenceAnchor(
        SyntaxTextLocation location,
        SyntaxSpan span) {
    public ObjectEvidenceAnchor {
        Objects.requireNonNull(location, "location");
    }
}
