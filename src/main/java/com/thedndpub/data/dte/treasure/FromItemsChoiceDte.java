package com.thedndpub.data.dte.treasure;

import java.util.List;

public record FromItemsChoiceDte(List<String> fromItems) implements MagicItemChooseDte {
    public FromItemsChoiceDte {
        fromItems = List.copyOf(fromItems);
    }
}
