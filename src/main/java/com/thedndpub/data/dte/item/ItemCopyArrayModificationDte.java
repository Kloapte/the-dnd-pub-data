package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ItemCopyArrayModificationDeserializer.class)
public record ItemCopyArrayModificationDte(
        String mode,
        Integer index,
        ItemCopyItemsDte items,
        Integer replaceIndex) {
}
