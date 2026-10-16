package com.thedndpub.data.dte.condition;

import java.util.List;

public record ConditionsDiseasesFileDte(
        List<ConditionDte> condition,
        List<DiseaseDte> disease,
        List<StatusDte> status) {
}
