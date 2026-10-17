package com.thedndpub.data.dto.charoption;

import java.util.Set;

/** Source-authored character option type code. The code is preserved verbatim. */
public record CharacterOptionType(String code) {
    private static final Set<String> OBSERVED_CODES = Set.of("CS", "DG", "RF:B", "SG");

    public CharacterOptionType {
        if (code == null || !OBSERVED_CODES.contains(code)) {
            throw new IllegalArgumentException("Unsupported character option type code: " + code);
        }
    }
}
