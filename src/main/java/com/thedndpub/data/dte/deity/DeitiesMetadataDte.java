package com.thedndpub.data.dte.deity;

import java.util.List;

public record DeitiesMetadataDte(List<String> internalCopies) {
    public DeitiesMetadataDte {
        internalCopies = internalCopies == null ? List.of() : List.copyOf(internalCopies);
    }
}
