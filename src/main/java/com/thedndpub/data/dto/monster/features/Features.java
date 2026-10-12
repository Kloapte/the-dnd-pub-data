package com.thedndpub.data.dto.monster.features;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record Features(List<Feature> traits, List<Feature> actions, List<Feature> bonusActions,
                       List<Feature> reactions, List<Feature> legendaryActions, List<Feature> mythicActions,
                       List<Feature> variants, String actionNote, String reactionNote,
                       List<String> legendaryHeader, List<String> mythicHeader, List<String> reactionHeader) {
}
