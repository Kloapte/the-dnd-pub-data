package com.thedndpub.data.dte.spell;

import java.util.List;

public record ScalingLevelDiceDte(String label, List<ScalingLevelDiceValueDte> scaling) {
    public ScalingLevelDiceDte {
        scaling = List.copyOf(scaling);
    }
}
