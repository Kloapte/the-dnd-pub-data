package com.thedndpub.data.dte.feat;

import java.util.Map;

public record FeatSpellObjectNodeDte(Map<String, FeatSpellGrantNodeDte> values) implements FeatSpellGrantNodeDte {
}
