package com.thedndpub.data.dte.monster;

import java.util.*;

/**
 * Deterministic identity index and copy dependency validation.
 */
public final class MonsterCopyGraph {
    private final Map<MonsterIdentity, MonsterSourceRecord> records;
    private final Map<MonsterIdentity, MonsterIdentity> parents;

    public MonsterCopyGraph(List<MonsterSourceRecord> input) {
        Map<MonsterIdentity, MonsterSourceRecord> r = new TreeMap<>();
        for (var x : input) {
            if (r.putIfAbsent(x.identity(), x) != null) {
                throw fail(x, "ambiguous canonical identity " + x.identity());
            }
        }
        Map<MonsterIdentity, MonsterIdentity> p = new TreeMap<>();
        for (var x : r.values()) {
            if (x.dte().copy() != null) {
                var c = x.dte().copy();
                MonsterIdentity parent = MonsterIdentity.from(c.name(), c.source());
                if (!r.containsKey(parent)) {
                    throw fail(x, "missing copy parent " + parent);
                }
                p.put(x.identity(), parent);
            }
        }
        Set<MonsterIdentity> done = new HashSet<>();
        LinkedHashSet<MonsterIdentity> active = new LinkedHashSet<>();
        for (var id : r.keySet()) {
            visit(id, p, done, active, r);
        }
        records = Collections.unmodifiableMap(r);
        parents = Collections.unmodifiableMap(p);
    }

    private static void visit(MonsterIdentity id, Map<MonsterIdentity, MonsterIdentity> p, Set<MonsterIdentity> d, LinkedHashSet<MonsterIdentity> a, Map<MonsterIdentity, MonsterSourceRecord> r) {
        if (d.contains(id)) {
            return;
        }
        if (!a.add(id)) {
            throw fail(r.get(id), "copy cycle: " + a + " -> " + id);
        }
        MonsterIdentity parent = p.get(id);
        if (parent != null) {
            visit(parent, p, d, a, r);
        }
        a.remove(id);
        d.add(id);
    }

    private static MonsterResolutionException fail(MonsterSourceRecord r, String s) {
        var raw = r.original();
        return new MonsterResolutionException(raw.path("name").asText(r.identity().name()) + "|" + raw.path("source").asText(r.identity().source()) + " at " + r.location() + ": " + s);
    }

    public Map<MonsterIdentity, MonsterSourceRecord> records() {
        return records;
    }

    public Map<MonsterIdentity, MonsterIdentity> parents() {
        return parents;
    }
}
