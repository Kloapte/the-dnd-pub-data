package com.thedndpub.data.dto.item;

/** One authored Item bonus token and its exact integer interpretation. */
public record ItemSignedBonus(String authoredToken, int numericValue) {
}
