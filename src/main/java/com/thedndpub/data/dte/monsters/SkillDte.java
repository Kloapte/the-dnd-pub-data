package com.thedndpub.data.dte.monsters;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thedndpub.data.dte.monsters.deserializers.SkillOtherDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SkillDte(
        String perception,
        String arcana,
        String history,
        String medicine,
        String religion,
        String stealth,
        String athletics,
        String performance,
        String persuasion,
        String acrobatics,
        String insight,
        String intimidation,
        @JsonProperty("animal handling")
        String animalHandling,
        String investigation,
        String nature,
        String survival,
        String deception,
        @JsonProperty("sleight of hand")
        String sleightOfHand,
        @JsonDeserialize(using = SkillOtherDeserializer.class)
        OneOfDte other) {
}
