package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ItemNullableIntegerDeserializer.class)
public record ItemNullableIntegerDte(Integer value, boolean present) {
}
