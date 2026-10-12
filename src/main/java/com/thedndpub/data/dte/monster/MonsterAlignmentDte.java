package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = MonsterAlignmentDeserializer.class)
public record MonsterAlignmentDte(String code, java.util.List<String> codes, Integer chance, String special,
                                  String note) {
}
