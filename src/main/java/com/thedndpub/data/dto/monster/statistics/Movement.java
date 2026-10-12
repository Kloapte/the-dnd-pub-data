package com.thedndpub.data.dto.monster.statistics;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record Movement(Map<String, MovementValue> modes, Boolean canHover, List<AlternateMovement> alternate,
                       Integer choiceAmount, List<String> choiceModes, String choiceNote, String note) {
}
