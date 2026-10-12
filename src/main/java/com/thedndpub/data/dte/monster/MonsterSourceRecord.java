package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Typed import plus its lossless source object; the tree is defensively copied.
 */
public record MonsterSourceRecord(MonsterIdentity identity, MonsterSourceLocation location, MonsterDte dte,
                                  ObjectNode original) {
    public MonsterSourceRecord {
        original = original.deepCopy();
    }

    @Override
    public ObjectNode original() {
        return original.deepCopy();
    }
}
