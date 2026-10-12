package com.thedndpub.data.dto.monster.defenses;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record Defense(List<String> terms, Boolean conditional, String note, String preNote,
                      String postNote, String special, List<Defense> nested) {
}
