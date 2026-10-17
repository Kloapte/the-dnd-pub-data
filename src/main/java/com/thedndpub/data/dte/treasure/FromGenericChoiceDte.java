package com.thedndpub.data.dte.treasure;

import java.util.List;

public record FromGenericChoiceDte(List<String> fromGeneric) implements MagicItemChooseDte {
    public FromGenericChoiceDte {
        fromGeneric = List.copyOf(fromGeneric);
    }
}
