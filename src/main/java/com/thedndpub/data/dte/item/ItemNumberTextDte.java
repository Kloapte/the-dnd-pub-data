package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ItemNumberTextDeserializer.class)
public record ItemNumberTextDte(Integer number, String text) {
}

