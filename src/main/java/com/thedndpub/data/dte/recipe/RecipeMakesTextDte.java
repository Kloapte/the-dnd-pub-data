package com.thedndpub.data.dte.recipe;

import com.fasterxml.jackson.annotation.JsonValue;

public record RecipeMakesTextDte(@JsonValue String value) implements RecipeMakesDte {
}
