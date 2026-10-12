package com.thedndpub.data.dte.monster;

import java.util.List;

public record MonsterFeatureDte(String name, List<MonsterEntryDte> entries, String type, String source, Integer page,
                                MonsterTokenDte token,
                                @com.fasterxml.jackson.annotation.JsonProperty("_version") MonsterVersionReferenceDte version) {
}
