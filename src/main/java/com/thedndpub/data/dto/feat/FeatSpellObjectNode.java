package com.thedndpub.data.dto.feat;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public record FeatSpellObjectNode(Map<String, FeatSpellGrantNode> values) implements FeatSpellGrantNode {
    public FeatSpellObjectNode {
        values = Collections.unmodifiableMap(new TreeMap<>(values));
    }
}
