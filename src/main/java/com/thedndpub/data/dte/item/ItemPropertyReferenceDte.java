package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ItemPropertyReferenceDeserializer.class)
public record ItemPropertyReferenceDte(String uid, String note) {
}
