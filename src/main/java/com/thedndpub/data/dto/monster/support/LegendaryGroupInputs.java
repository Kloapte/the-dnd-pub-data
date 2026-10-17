package com.thedndpub.data.dto.monster.support;

import java.util.List;

public record LegendaryGroupInputs(
        List<LegendaryGroupAuthoredRecord> groups,
        List<LegendaryGroupAuthoredTemplate> templates) {
    public LegendaryGroupInputs {
        groups = List.copyOf(groups);
        templates = List.copyOf(templates);
    }
}
