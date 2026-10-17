package com.thedndpub.data.dto.spell;

import java.util.List;

public record SpellScaling(String label, List<SpellScalingValue> values) {
    public SpellScaling {
        values = List.copyOf(values);
    }
}
