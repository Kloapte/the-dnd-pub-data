package com.thedndpub.data.dto.item;

import com.thedndpub.data.dte.item.BaseItemsFileDte;
import com.thedndpub.data.dte.item.ItemFileDte;

/** Internal imported selectors and lookup definitions kept outside the public item definitions. */
public record ItemAuxiliaryData(ItemFileDte items, BaseItemsFileDte baseItems) {
}
