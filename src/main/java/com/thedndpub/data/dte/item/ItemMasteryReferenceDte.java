package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ItemMasteryReferenceDeserializer.class)
public record ItemMasteryReferenceDte(String uid, String note) {
}
