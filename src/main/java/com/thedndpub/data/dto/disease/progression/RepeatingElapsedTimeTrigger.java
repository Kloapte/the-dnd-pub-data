package com.thedndpub.data.dto.disease.progression;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseInterval;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RepeatingElapsedTimeTrigger(String kind, DiseaseInterval interval, String relativeTo)
        implements DiseaseProgressionTrigger {
}
