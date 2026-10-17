package com.thedndpub.data.dte.object;

import java.util.List;

public record ObjectActionListDte(String type, Integer page, String style, List<ObjectActionItemDte> items)
        implements ObjectActionContentDte {
    public ObjectActionListDte {
        items = items == null ? null : List.copyOf(items);
    }
}
