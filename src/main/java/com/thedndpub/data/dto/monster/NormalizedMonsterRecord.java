package com.thedndpub.data.dto.monster;

import com.thedndpub.data.dto.monster.internal.MonsterClassification;

/**
 * A normalized definition paired with its internal import lineage.
 */
public record NormalizedMonsterRecord(MonsterDto monster, MonsterImportProvenance provenance,
                                     MonsterClassification classification) {
}
