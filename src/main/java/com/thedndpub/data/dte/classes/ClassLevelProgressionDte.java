package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;

@JsonDeserialize(using = ClassLevelProgressionDeserializer.class)
public record ClassLevelProgressionDte(
        List<Integer> dense,
        Map<Integer, Integer> milestones) {
    public ClassLevelProgressionDte {
        dense = ClassDteCollections.list(dense);
        milestones = ClassDteCollections.map(milestones);
    }
}
