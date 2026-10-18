package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.item.syntax.ItemChargeSyntaxIndex;
import com.thedndpub.data.importer.item.ItemChargeSyntaxAdapter;
import org.springframework.stereotype.Component;

/** Startup-loaded internal syntax observations for authored Item source roots. */
@Component
public final class ItemChargeSyntaxCatalog {
    private final ItemChargeSyntaxIndex index;

    public ItemChargeSyntaxCatalog(ObjectMapper objectMapper, ItemCatalog itemCatalog) {
        index = new ItemChargeSyntaxAdapter(objectMapper).adapt(itemCatalog);
    }

    public ItemChargeSyntaxIndex index() {
        return index;
    }
}
