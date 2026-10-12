package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MonsterCopyReferenceDte(String name, String source,
                                      @JsonProperty("_mod") MonsterCopyModDte mod,
                                      @JsonProperty("_preserve") MonsterCopyPreserveDte preserve,
                                      @JsonProperty("_templates") List<MonsterTemplateReferenceDte> templates) {
}
