package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionChoiceTriggerKind {
    @JsonProperty("CHECK_SUCCEEDED") CHECK_SUCCEEDED,
    @JsonProperty("SAVE_FAILED") SAVE_FAILED,
    @JsonProperty("TRIGGER_OCCURRED") TRIGGER_OCCURRED,
    @JsonProperty("ACTION_TAKEN") ACTION_TAKEN
}
