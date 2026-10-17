package com.thedndpub.data.dte.recipe;

import com.fasterxml.jackson.annotation.JsonValue;

public record RecipeCookNoteTextDte(@JsonValue String value) implements RecipeCookNoteEntryDte {
}
