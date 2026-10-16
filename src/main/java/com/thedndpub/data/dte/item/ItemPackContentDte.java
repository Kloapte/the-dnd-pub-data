package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ItemPackContentDeserializer.class)
public record ItemPackContentDte(String item, Integer quantity, String special) {
}
