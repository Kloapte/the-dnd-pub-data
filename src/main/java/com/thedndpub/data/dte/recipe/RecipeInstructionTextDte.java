package com.thedndpub.data.dte.recipe;

import com.fasterxml.jackson.annotation.JsonValue;

public record RecipeInstructionTextDte(@JsonValue String value) implements RecipeInstructionEntryDte {
}
