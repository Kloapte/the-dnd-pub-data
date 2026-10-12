package com.thedndpub.data.dto.monster;

/**
 * A normalized definition paired with its internal import lineage.
 */
public record NormalizedMonsterRecord(MonsterDto monster, MonsterImportProvenance provenance) {
}
