package com.thedndpub.data.dte.object;

import java.util.List;

public record ObjectAttackDte(String type, String attackType, List<String> attackEntries, List<String> hitEntries)
        implements ObjectActionContentDte {
    public ObjectAttackDte {
        attackEntries = attackEntries == null ? null : List.copyOf(attackEntries);
        hitEntries = hitEntries == null ? null : List.copyOf(hitEntries);
    }
}
