package com.thedndpub.data.references;

import java.util.Objects;

/** Source-authored reference with the unmodified token and any parsed identity. */
public record AuthoredReference(
        ReferenceOwner owner,
        String fieldLocator,
        ReferenceTargetKind targetKind,
        ReferenceSemanticForm semanticForm,
        String originalToken,
        String originalSource,
        String parsedName,
        String parsedSource,
        String deferredReason) {
    public AuthoredReference {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(fieldLocator, "fieldLocator");
        Objects.requireNonNull(targetKind, "targetKind");
        Objects.requireNonNull(semanticForm, "semanticForm");
        Objects.requireNonNull(originalToken, "originalToken");
        if (parsedName != null && parsedName.isBlank()) {
            throw new IllegalArgumentException("Parsed name must not be blank");
        }
    }

    public AuthoredReferenceIdentity authoredIdentity() {
        return new AuthoredReferenceIdentity(originalToken, originalSource);
    }

    public ParsedSourceQualifiedIdentity sourceQualifiedIdentity() {
        if (parsedName == null || parsedSource == null) {
            return null;
        }
        return new ParsedSourceQualifiedIdentity(parsedName, parsedSource,
                ReferenceUidParser.parse(originalToken).configuration());
    }
}
