package com.thedndpub.data.dto.item;

import java.util.regex.Pattern;

public final class ItemSignedBonusParser {
    private static final Pattern SIGNED_INTEGER = Pattern.compile("[+-][0-9]+");

    private ItemSignedBonusParser() {
    }

    public static ItemSignedBonus parse(String authoredToken, String sourceField) {
        if (authoredToken == null) {
            return null;
        }
        if (!SIGNED_INTEGER.matcher(authoredToken).matches()) {
            throw new IllegalArgumentException(
                    "Unsupported signed bonus token for " + sourceField + ": " + authoredToken);
        }

        try {
            return new ItemSignedBonus(authoredToken, Integer.parseInt(authoredToken));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Signed bonus token is outside the integer range for " + sourceField + ": " + authoredToken,
                    exception);
        }
    }
}
