package com.thedndpub.data.dte.source;

import java.util.List;

public record SourceCodeDte(String code, List<String> aliases) {
    public SourceCodeDte {
        aliases = aliases == null ? List.of() : List.copyOf(aliases);
    }
}
