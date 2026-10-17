package com.thedndpub.data.references;

public final class ReferenceUidParser {
    private ReferenceUidParser() {
    }

    public static ParsedUid parse(String originalToken) {
        if (originalToken == null || originalToken.isBlank()) {
            throw new IllegalArgumentException("Reference token must not be blank");
        }
        String[] identityAndConfiguration = originalToken.split("#", 2);
        String identity = identityAndConfiguration[0];
        String configuration = identityAndConfiguration.length == 2 ? identityAndConfiguration[1] : null;
        String[] parts = identity.split("\\|", -1);
        if (parts.length > 2 || parts[0].isBlank()) {
            throw new IllegalArgumentException("Malformed reference UID: " + originalToken);
        }
        if (parts.length == 1) {
            return new ParsedUid(originalToken, parts[0], null, configuration);
        }
        if (parts[1].isBlank()) {
            throw new IllegalArgumentException("Malformed source-qualified reference UID: " + originalToken);
        }
        return new ParsedUid(originalToken, parts[0], parts[1], configuration);
    }
}
