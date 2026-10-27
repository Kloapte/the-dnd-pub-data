package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionSkillTask {
    @JsonProperty("CREATURE_STATE_OF_MIND") CREATURE_STATE_OF_MIND,
    @JsonProperty("AILMENT_OR_CAUSE_OF_DEATH") AILMENT_OR_CAUSE_OF_DEATH,
    @JsonProperty("CONCEALED_CREATURE_OR_OBJECT") CONCEALED_CREATURE_OR_OBJECT,
    @JsonProperty("TRACKS_OR_FOOD") TRACKS_OR_FOOD,
    @JsonProperty("SPELLS") SPELLS,
    @JsonProperty("MAGIC_ITEMS") MAGIC_ITEMS,
    @JsonProperty("ELDRITCH_SYMBOLS") ELDRITCH_SYMBOLS,
    @JsonProperty("MAGICAL_TRADITIONS") MAGICAL_TRADITIONS,
    @JsonProperty("PLANES_OF_EXISTENCE") PLANES_OF_EXISTENCE,
    @JsonProperty("HISTORIC_EVENTS_AND_PEOPLE") HISTORIC_EVENTS_AND_PEOPLE,
    @JsonProperty("ANCIENT_CIVILIZATIONS") ANCIENT_CIVILIZATIONS,
    @JsonProperty("WARS") WARS,
    @JsonProperty("TRAPS") TRAPS,
    @JsonProperty("CIPHERS") CIPHERS,
    @JsonProperty("RIDDLES") RIDDLES,
    @JsonProperty("GADGETRY") GADGETRY,
    @JsonProperty("TERRAIN") TERRAIN,
    @JsonProperty("FLORA") FLORA,
    @JsonProperty("WEATHER") WEATHER,
    @JsonProperty("DEITIES") DEITIES,
    @JsonProperty("RELIGIOUS_HIERARCHIES_AND_RITES") RELIGIOUS_HIERARCHIES_AND_RITES,
    @JsonProperty("HOLY_SYMBOLS") HOLY_SYMBOLS,
    @JsonProperty("CULTS") CULTS
}
