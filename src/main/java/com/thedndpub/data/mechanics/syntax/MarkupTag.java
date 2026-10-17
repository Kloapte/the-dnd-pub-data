package com.thedndpub.data.mechanics.syntax;

import java.util.List;
import java.util.Objects;

/** Lossless tag syntax. No component describes an applied gameplay effect. */
public record MarkupTag(
        SyntaxEvidence evidence,
        String originalText,
        String name,
        String rawArgumentText,
        List<String> arguments,
        List<MarkupTag> children) {
    public MarkupTag {
        Objects.requireNonNull(evidence, "evidence");
        Objects.requireNonNull(originalText, "originalText");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(rawArgumentText, "rawArgumentText");
        arguments = List.copyOf(arguments);
        children = List.copyOf(children);
        if (originalText.length() != evidence.span().endExclusive() - evidence.span().startInclusive()) {
            throw new IllegalArgumentException("Original tag length must match its UTF-16 span");
        }
        int previousEnd = evidence.span().startInclusive();
        for (MarkupTag child : children) {
            if (!evidence.location().equals(child.evidence().location())
                    || child.evidence().span().startInclusive() < previousEnd
                    || child.evidence().span().endExclusive() >= evidence.span().endExclusive()) {
                throw new IllegalArgumentException("Nested tag boundaries must belong to the original parent");
            }
            previousEnd = child.evidence().span().endExclusive();
        }
    }
}
