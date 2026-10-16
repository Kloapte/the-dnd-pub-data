package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/** Source names are dynamic metadata keys; their payload is the known boolean marker. */
public final class ItemMetadataSourcesDte {
    private final Map<String, Boolean> sourceFlags = new TreeMap<>();

    @JsonAnySetter
    public void addSource(String source, Boolean present) {
        sourceFlags.put(source, present);
    }

    public Map<String, Boolean> sourceFlags() {
        return Collections.unmodifiableMap(sourceFlags);
    }
}
