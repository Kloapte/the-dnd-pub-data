package com.thedndpub.data.dte.background;

import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.TreeMap;

public record BackgroundAdditionalSpellDte(Map<String, List<String>> expanded) {
    public BackgroundAdditionalSpellDte {
        TreeMap<String, List<String>> copied = new TreeMap<>();
        expanded.forEach((level, spells) -> copied.put(level, List.copyOf(spells)));
        expanded = Collections.unmodifiableMap(copied);
    }
}
