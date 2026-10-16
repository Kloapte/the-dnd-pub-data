package com.thedndpub.data.dte.classes;

import java.util.List;

public record ClassNameChoiceDte(
        List<String> from,
        Integer count) {
    public ClassNameChoiceDte {
        from = ClassDteCollections.list(from);
    }
}
