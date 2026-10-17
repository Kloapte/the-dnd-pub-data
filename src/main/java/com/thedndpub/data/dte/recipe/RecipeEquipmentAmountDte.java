package com.thedndpub.data.dte.recipe;

import java.math.BigDecimal;

public record RecipeEquipmentAmountDte(String type, String entry, BigDecimal amount1)
        implements RecipeEquipmentEntryDte {
}
