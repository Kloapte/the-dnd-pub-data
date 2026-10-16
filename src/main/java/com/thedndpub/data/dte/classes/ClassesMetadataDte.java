package com.thedndpub.data.dte.classes;

import java.util.List;

public record ClassesMetadataDte(
        List<String> internalCopies) {
    public ClassesMetadataDte {
        internalCopies = ClassDteCollections.list(internalCopies);
    }
}
