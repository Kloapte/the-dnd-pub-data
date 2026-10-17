package com.thedndpub.data.dte.recipe;

import java.math.BigDecimal;

public record RecipeIngredientAmountDte(
        String type,
        String entry,
        BigDecimal amount1,
        BigDecimal amount2,
        BigDecimal amount3,
        BigDecimal amount4,
        BigDecimal amount5,
        Integer page) implements RecipeIngredientEntryDte, RecipeIngredientLeafDte {
}
