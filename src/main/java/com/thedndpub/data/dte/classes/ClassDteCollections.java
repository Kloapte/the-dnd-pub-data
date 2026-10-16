package com.thedndpub.data.dte.classes;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Defensive copies that retain absent versus explicitly empty source collections. */
public final class ClassDteCollections {
    private ClassDteCollections() {
    }

    public static <T> List<T> list(List<T> values) {
        return values == null ? null : List.copyOf(values);
    }

    public static <T> List<List<T>> rows(List<List<T>> values) {
        return values == null ? null : values.stream().map(List::copyOf).toList();
    }

    public static <K, V> Map<K, V> map(Map<K, V> values) {
        return values == null ? null : Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }

    public static <K, V> Map<K, List<V>> mapOfLists(Map<K, List<V>> values) {
        if (values == null) {
            return null;
        }
        Map<K, List<V>> copy = new LinkedHashMap<>();
        values.forEach((key, value) -> copy.put(key, List.copyOf(value)));
        return Collections.unmodifiableMap(copy);
    }

    public static <K, S, V> Map<K, Map<S, V>> nestedMap(Map<K, Map<S, V>> values) {
        if (values == null) {
            return null;
        }
        Map<K, Map<S, V>> copy = new LinkedHashMap<>();
        values.forEach((key, value) -> copy.put(key, map(value)));
        return Collections.unmodifiableMap(copy);
    }
}
