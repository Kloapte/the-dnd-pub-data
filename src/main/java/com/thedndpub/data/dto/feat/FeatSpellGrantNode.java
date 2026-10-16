package com.thedndpub.data.dto.feat;

public sealed interface FeatSpellGrantNode permits FeatSpellTextNode, FeatSpellArrayNode,
        FeatSpellObjectNode, FeatSpellCountNode, FeatSpellFlagNode {
}
