package com.thedndpub.data.dte.traphazard;

import java.util.List;

public record TrapStageGroupDte(String type, String style, List<TrapStageItemDte> items) implements TrapStageDte {
    public TrapStageGroupDte {
        items = items == null ? null : List.copyOf(items);
    }
}
