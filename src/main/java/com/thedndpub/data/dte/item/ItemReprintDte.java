package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ItemReprintDeserializer.class)
public record ItemReprintDte(String uid, String tag) {
}
