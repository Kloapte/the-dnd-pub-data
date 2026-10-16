package com.thedndpub.data.dto.item;

import com.thedndpub.data.dte.item.ItemCopyArrayModificationDte;
import com.thedndpub.data.dte.item.ItemCopyDte;
import com.thedndpub.data.dte.item.ItemSourceDte;
import com.thedndpub.data.dte.item.ItemPropertyReferenceDte;
import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.common.entry.TextEntryDte;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public final class ItemCopyResolver {
    public List<ResolvedItemDte> resolve(List<ItemSourceDte> roots) {
        Map<String, ItemSourceDte> byIdentity = new HashMap<>();
        for (ItemSourceDte root : roots) {
            String key = identity(root.name(), root.source());
            if (byIdentity.putIfAbsent(key, root) != null) {
                throw new IllegalStateException("Duplicate item copy resolution identity: " + key);
            }
        }
        Map<String, ResolvedItemDte> resolved = new HashMap<>();
        List<ResolvedItemDte> result = new ArrayList<>();
        for (ItemSourceDte root : roots) {
            result.add(resolve(root, byIdentity, resolved, new ArrayList<>()));
        }
        return List.copyOf(result);
    }

    private ResolvedItemDte resolve(
            ItemSourceDte root,
            Map<String, ItemSourceDte> byIdentity,
            Map<String, ResolvedItemDte> resolved,
            List<String> visiting) {
        String key = identity(root.name(), root.source());
        ResolvedItemDte existing = resolved.get(key);
        if (existing != null) {
            return existing;
        }
        if (visiting.contains(key)) {
            throw new IllegalStateException("Cyclic item copy chain: " + String.join(" -> ", visiting) + " -> " + key);
        }
        ItemCopyDte copy = root.copy();
        ResolvedItemDte parent = null;
        if (copy != null) {
            if (copy.name() == null || copy.source() == null) {
                throw new IllegalStateException("Item copy target must declare both name and source: " + key);
            }
            String parentKey = identity(copy.name(), copy.source());
            ItemSourceDte parentSource = byIdentity.get(parentKey);
            if (parentSource == null) {
                throw new IllegalStateException("Item copy target not found: " + copy.name() + " | " + copy.source());
            }
            visiting.add(key);
            parent = resolve(parentSource, byIdentity, resolved, visiting);
            visiting.remove(visiting.size() - 1);
            validateModifications(copy, parent);
        }
        List<ItemPropertyReferenceDte> effectiveProperties = parent == null
                ? new ArrayList<>() : new ArrayList<>(parent.resolvedProperties());
        ItemCopyArrayModificationDte propertyModification = copy == null || copy.modification() == null
                ? null : copy.modification().property();
        if (propertyModification != null) {
            String uid = propertyModification.items().text();
            boolean exists = effectiveProperties.stream().anyMatch(value -> value.uid().equals(uid));
            if (!exists) {
                effectiveProperties.add(new ItemPropertyReferenceDte(uid, null));
            }
        }
        if (root.property() != null) {
            effectiveProperties = new ArrayList<>(root.property());
        }
        List<EntryDte> effectiveEntries = parent == null
                ? new ArrayList<>() : new ArrayList<>(parent.resolvedEntries());
        ItemCopyArrayModificationDte entryModification = copy == null || copy.modification() == null
                ? null : copy.modification().entries();
        if (entryModification != null) {
            applyEntryModification(effectiveEntries, entryModification, root.name());
        }
        if (root.entries() != null) {
            effectiveEntries = new ArrayList<>(root.entries());
        }
        ResolvedItemDte value = new ResolvedItemDte(root, parent, copy != null,
                effectiveProperties, effectiveEntries);
        resolved.put(key, value);
        return value;
    }

    private static void validateModifications(ItemCopyDte copy, ResolvedItemDte parent) {
        if (copy.modification() == null) {
            return;
        }
        ItemCopyArrayModificationDte entries = copy.modification().entries();
        if (entries != null) {
            int count = parent.resolvedEntries().size();
            switch (entries.mode()) {
                case "appendArr" -> {
                    requireItems(entries, copy.name());
                    requireNoIndices(entries, copy.name());
                }
                case "insertArr" -> {
                    requireItems(entries, copy.name());
                    if (entries.replaceIndex() != null) {
                        throw new IllegalStateException("insertArr cannot contain replace.index: " + copy.name());
                    }
                    int index = entries.index() == null ? Integer.MIN_VALUE : entries.index();
                    if (index != -1 && (index < 0 || index > count)) {
                        throw new IllegalStateException("Invalid entries.insertArr index " + index + " for " + copy.name());
                    }
                }
                case "replaceArr" -> {
                    requireItems(entries, copy.name());
                    if (entries.index() != null) {
                        throw new IllegalStateException("replaceArr cannot contain index: " + copy.name());
                    }
                    Integer index = entries.replaceIndex();
                    if (index == null || index < 0 || index >= count) {
                        throw new IllegalStateException("Invalid entries.replaceArr index " + index + " for " + copy.name());
                    }
                }
                default -> throw new IllegalStateException("Unsupported item copy entries operation: " + entries.mode());
            }
        }
        ItemCopyArrayModificationDte properties = copy.modification().property();
        if (properties != null) {
            if (!"appendIfNotExistsArr".equals(properties.mode()) || properties.items() == null
                    || properties.items().text() == null || properties.index() != null
                    || properties.replaceIndex() != null) {
                throw new IllegalStateException("Unsupported item copy property operation: " + properties.mode());
            }
        }
    }

    private static void requireItems(ItemCopyArrayModificationDte modification, String itemName) {
        if (modification.items() == null) {
            throw new IllegalStateException("Item copy operation has no items: " + itemName);
        }
    }

    private static void requireNoIndices(ItemCopyArrayModificationDte modification, String itemName) {
        if (modification.index() != null || modification.replaceIndex() != null) {
            throw new IllegalStateException("appendArr cannot contain array indices: " + itemName);
        }
    }

    private static void applyEntryModification(
            List<EntryDte> entries, ItemCopyArrayModificationDte modification, String itemName) {
        List<EntryDte> additions = modification.items().text() == null
                ? modification.items().entries() : List.of(new TextEntryDte(modification.items().text()));
        switch (modification.mode()) {
            case "appendArr" -> entries.addAll(additions);
            case "insertArr" -> {
                int requestedIndex = modification.index();
                int index = requestedIndex == -1 ? entries.size() : requestedIndex;
                if (index > entries.size()) {
                    throw new IllegalStateException("Invalid entries.insertArr index for " + itemName);
                }
                entries.addAll(index, additions);
            }
            case "replaceArr" -> {
                int index = modification.replaceIndex();
                entries.remove(index);
                entries.addAll(index, additions);
            }
            default -> throw new IllegalStateException("Unsupported item copy entries operation: "
                    + modification.mode());
        }
    }

    public static String identity(String name, String source) {
        return normalize(name) + "\u0000" + normalize(source);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
