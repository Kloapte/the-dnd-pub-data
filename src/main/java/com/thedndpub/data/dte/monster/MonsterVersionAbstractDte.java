package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MonsterVersionAbstractDte(String name, String source, @JsonProperty("_mod") MonsterCopyModDte mod) {
}
