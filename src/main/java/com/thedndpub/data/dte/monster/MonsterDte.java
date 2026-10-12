package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Raw, strongly typed 5eTools monster import record.
 */
@JsonDeserialize(using = MonsterDteDeserializer.class)
public record MonsterDte(
        MonsterIdentityDte identity,
        MonsterStatisticsDte statistics,
        MonsterDefensesDte defenses,
        MonsterFeaturesDte features,
        MonsterTagsDte tags,
        MonsterSupplementalDte supplemental,
        @com.fasterxml.jackson.annotation.JsonProperty("_copy") MonsterCopyReferenceDte copy,
        @com.fasterxml.jackson.annotation.JsonProperty("_versions") java.util.List<MonsterVersionDte> versions
) {
}
