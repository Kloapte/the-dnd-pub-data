package com.thedndpub.data.references;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

abstract class AbstractIndexedTargetAdapter implements CatalogTargetAdapter {
    private final ReferenceTargetKind targetKind;
    private final Map<String, List<TargetIdentity>> byIdentity;
    private final Map<String, List<TargetIdentity>> byName;
    private final List<TargetIdentity> identities;

    AbstractIndexedTargetAdapter(ReferenceTargetKind targetKind, List<TargetIdentity> identities) {
        this.targetKind = targetKind;
        Map<String, TargetIdentity> byId = new TreeMap<>();
        Map<String, List<TargetIdentity>> exact = new TreeMap<>();
        Map<String, List<TargetIdentity>> names = new TreeMap<>();
        for (TargetIdentity identity : identities) {
            if (identity.targetKind() != targetKind
                    && !(targetKind == ReferenceTargetKind.ITEM
                    && identity.targetKind() == ReferenceTargetKind.BASE_ITEM)) {
                throw new IllegalArgumentException("Target kind does not match adapter kind");
            }
            if (byId.putIfAbsent(identity.stableId(), identity) != null) {
                throw new IllegalStateException("Duplicate target stable ID: " + identity.stableId());
            }
            exact.computeIfAbsent(key(identity.name(), identity.source()), ignored -> new ArrayList<>())
                    .add(identity);
            names.computeIfAbsent(normalize(identity.name()), ignored -> new ArrayList<>()).add(identity);
        }
        byIdentity = immutableIndex(exact);
        byName = immutableIndex(names);
        this.identities = byId.values().stream().toList();
    }

    @Override
    public final ReferenceTargetKind targetKind() {
        return targetKind;
    }

    @Override
    public final List<TargetIdentity> exact(String name, String source) {
        return byIdentity.getOrDefault(key(name, source), List.of());
    }

    @Override
    public final List<TargetIdentity> candidates(String name) {
        return byName.getOrDefault(normalize(name), List.of());
    }

    @Override
    public final List<TargetIdentity> identities() {
        return identities;
    }

    private static Map<String, List<TargetIdentity>> immutableIndex(Map<String, List<TargetIdentity>> values) {
        Map<String, List<TargetIdentity>> result = new TreeMap<>();
        values.forEach((key, targets) -> result.put(key, targets.stream()
                .sorted(Comparator.comparing(TargetIdentity::stableId)).toList()));
        return Collections.unmodifiableMap(result);
    }

    private static String key(String name, String source) {
        return normalize(name) + "\u0000" + normalize(source);
    }

    private static String normalize(String value) {
        return Normalizer.normalize(value.trim(), Normalizer.Form.NFC).toLowerCase(Locale.ROOT);
    }
}
