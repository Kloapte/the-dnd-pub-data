package com.thedndpub.data.dte.item;

import java.util.List;
import java.util.Map;

public record ItemContainerCapacityDte(
        List<Double> weight,
        Boolean weightless,
        List<Double> volume,
        List<Map<String, Integer>> item) {
}
