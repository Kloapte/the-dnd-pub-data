package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ItemBooleanTextDeserializer.class)
public record ItemBooleanTextDte(Boolean flag, String text) {
}
