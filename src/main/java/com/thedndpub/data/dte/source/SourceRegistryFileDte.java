package com.thedndpub.data.dte.source;

import java.util.List;

public record SourceRegistryFileDte(List<SourceCodeDte> sources) {
    public SourceRegistryFileDte {
        sources = sources == null ? List.of() : List.copyOf(sources);
    }
}
