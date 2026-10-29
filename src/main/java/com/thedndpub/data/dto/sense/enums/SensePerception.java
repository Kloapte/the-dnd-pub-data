package com.thedndpub.data.dto.sense.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SensePerception {
    @JsonProperty("visible-creatures") VISIBLE_CREATURES,
    @JsonProperty("invisible-creatures") INVISIBLE_CREATURES,
    @JsonProperty("objects") OBJECTS,
    @JsonProperty("colors") COLORS,
    @JsonProperty("visual-detail") VISUAL_DETAIL,
    @JsonProperty("illusions") ILLUSIONS,
    @JsonProperty("true-forms") TRUE_FORMS,
    @JsonProperty("ethereal-plane") ETHEREAL_PLANE,
    @JsonProperty("invisible-objects") INVISIBLE_OBJECTS
}
