package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ItemNullableStringDeserializer.class)
public record ItemNullableStringDte(String value, boolean present) {
}
