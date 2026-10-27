package com.thedndpub.data.dto.rule.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleReferenceType {
    @JsonProperty("action") ACTION,
    @JsonProperty("background") BACKGROUND,
    @JsonProperty("class") CLASS,
    @JsonProperty("class-feature") CLASS_FEATURE,
    @JsonProperty("condition") CONDITION,
    @JsonProperty("creature") CREATURE,
    @JsonProperty("disease") DISEASE,
    @JsonProperty("feat") FEAT,
    @JsonProperty("item") ITEM,
    @JsonProperty("language") LANGUAGE,
    @JsonProperty("object") OBJECT,
    @JsonProperty("optional-feature") OPTIONAL_FEATURE,
    @JsonProperty("rule") RULE,
    @JsonProperty("sense") SENSE,
    @JsonProperty("skill") SKILL,
    @JsonProperty("source") SOURCE,
    @JsonProperty("spell") SPELL,
    @JsonProperty("status") STATUS,
    @JsonProperty("subclass") SUBCLASS,
    @JsonProperty("subclass-feature") SUBCLASS_FEATURE,
    @JsonProperty("vehicle") VEHICLE,
    @JsonProperty("race") RACE
}
