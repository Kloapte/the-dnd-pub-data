package com.thedndpub.data.dto.feat;

import java.util.List;

public record FeatSpellArrayNode(List<FeatSpellGrantNode> values) implements FeatSpellGrantNode {
    public FeatSpellArrayNode {
        values = List.copyOf(values);
    }
}
