package com.thedndpub.data.dte.recipe;

import com.fasterxml.jackson.annotation.JsonValue;

import java.math.BigDecimal;

public record RecipeMakesQuantityDte(@JsonValue BigDecimal value) implements RecipeMakesDte {
}
