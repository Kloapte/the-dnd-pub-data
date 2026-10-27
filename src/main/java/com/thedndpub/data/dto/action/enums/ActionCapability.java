package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionCapability {
    @JsonProperty("ATTACK_MELEE") ATTACK_MELEE,
    @JsonProperty("ATTACK_RANGED") ATTACK_RANGED,
    @JsonProperty("ATTACK_BAREHANDS") ATTACK_BAREHANDS,
    @JsonProperty("MARK_TARGET") MARK_TARGET,
    @JsonProperty("DASH") DASH,
    @JsonProperty("DISENGAGE") DISENGAGE,
    @JsonProperty("DODGE") DODGE,
    @JsonProperty("HELP") HELP,
    @JsonProperty("HIDE") HIDE,
    @JsonProperty("READY") READY,
    @JsonProperty("SEARCH") SEARCH,
    @JsonProperty("STUDY") STUDY,
    @JsonProperty("INFLUENCE") INFLUENCE,
    @JsonProperty("UTILIZE") UTILIZE,
    @JsonProperty("CAST_SPELL") CAST_SPELL,
    @JsonProperty("END_CONCENTRATION") END_CONCENTRATION,
    @JsonProperty("ESCAPE_GRAPPLE") ESCAPE_GRAPPLE,
    @JsonProperty("USE_OBJECT") USE_OBJECT,
    @JsonProperty("GRAPPLE") GRAPPLE,
    @JsonProperty("SHOVE") SHOVE,
    @JsonProperty("STABILIZE") STABILIZE,
    @JsonProperty("MAGIC_ACTION") MAGIC_ACTION,
    @JsonProperty("DON_SHIELD") DON_SHIELD,
    @JsonProperty("DOFF_SHIELD") DOFF_SHIELD,
    @JsonProperty("ACTIVATE_MAGIC_ITEM") ACTIVATE_MAGIC_ITEM,
    @JsonProperty("IDENTIFY_SPELL") IDENTIFY_SPELL,
    @JsonProperty("IMPROVISE") IMPROVISE,
    @JsonProperty("TRAVERSE_CREATURE_SPACE") TRAVERSE_CREATURE_SPACE,
    @JsonProperty("ENTER_CREATURE_SPACE") ENTER_CREATURE_SPACE
}
