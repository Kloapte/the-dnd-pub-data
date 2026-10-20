package com.thedndpub.data.mechanics.benchmark;

import com.thedndpub.data.mechanics.model.ModelChecks;

import java.util.List;

public record ExtractionScope(String id, String version, List<ExtractionPattern> patterns) {
    public ExtractionScope {
        ModelChecks.text(id, "id");
        ModelChecks.text(version, "version");
        patterns = ModelChecks.list(patterns);
        ModelChecks.require(patterns.stream().distinct().count() == patterns.size(), "Duplicate scope pattern");
    }

    public boolean includes(ExtractionPattern pattern) {
        return patterns.contains(pattern);
    }
}
