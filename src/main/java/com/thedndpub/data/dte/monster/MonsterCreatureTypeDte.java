package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = MonsterCreatureTypeDeserializer.class)
public record MonsterCreatureTypeDte(String type, MonsterCreatureTypeChoiceDte typeChoice,
                                     java.util.List<MonsterCreatureTypeTagDte> tags, String swarmSize,
                                     Boolean sidekickHidden,
                                     java.util.List<String> sidekickTags, String sidekickType) {
}
