package com.thedndpub.data.dto.sense.capabilities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.sense.enums.SenseIllusionAppearance;
import com.thedndpub.data.dto.sense.enums.SenseIllusionDetection;
import com.thedndpub.data.dto.sense.enums.SenseSavingThrowOutcome;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SenseIllusionInteraction(
        SenseIllusionAppearance appearance,
        SenseIllusionDetection detection,
        SenseSavingThrowOutcome savingThrowOutcome) {
    public SenseIllusionInteraction {
        if (appearance == null && detection == null && savingThrowOutcome == null) {
            throw new IllegalArgumentException("Illusion interaction requires at least one declared property");
        }
        if (appearance != null) Objects.requireNonNull(appearance);
    }
}
