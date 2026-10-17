package com.thedndpub.data.dte.treasure;

import java.util.List;

public record FromGroupChoiceDte(List<String> fromGroup) implements MagicItemChooseDte {
    public FromGroupChoiceDte {
        fromGroup = List.copyOf(fromGroup);
    }
}
