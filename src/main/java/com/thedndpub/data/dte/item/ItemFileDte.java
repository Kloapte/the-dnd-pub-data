package com.thedndpub.data.dte.item;

import java.util.List;

public record ItemFileDte(
        ItemMetadataDte _meta,
        List<ItemSourceDte> item,
        List<ItemSourceDte> itemGroup) {
}
