package com.thedndpub.data.mechanics.syntax;

public final class MarkupSyntaxException extends IllegalArgumentException {
    private final SyntaxEvidence evidence;

    public MarkupSyntaxException(String reason, SyntaxEvidence evidence) {
        super(reason + " at " + evidence.location().owner().resourcePath()
                + "#" + evidence.location().jsonPointer() + ":" + evidence.span().startInclusive());
        this.evidence = evidence;
    }

    public SyntaxEvidence evidence() {
        return evidence;
    }
}
