package com.thedndpub.data.dto.background;

import com.thedndpub.data.dte.background.BackgroundCopyDte;
import com.thedndpub.data.dte.background.BackgroundDte;
import com.thedndpub.data.dte.background.BackgroundEntryModificationDte;
import com.thedndpub.data.dte.background.BackgroundsFileDte;
import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.common.entry.NamedEntryDte;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class BackgroundCopyResolver {
    public List<ResolvedBackgroundDte> resolve(BackgroundsFileDte file) {
        if (file.background() == null) {
            throw new IllegalStateException("Background source has no background collection");
        }
        Map<String, BackgroundDte> byIdentity = new HashMap<>();
        for (BackgroundDte background : file.background()) {
            String identity = identity(background.name(), background.source());
            if (byIdentity.putIfAbsent(identity, background) != null) {
                throw new IllegalStateException("Duplicate background name/source identity: " + identity);
            }
        }
        Map<String, ResolvedBackgroundDte> resolved = new HashMap<>();
        for (BackgroundDte background : file.background()) {
            resolve(background, byIdentity, resolved, new HashSet<>());
        }
        return file.background().stream()
                .map(background -> resolved.get(identity(background.name(), background.source())))
                .toList();
    }

    private ResolvedBackgroundDte resolve(
            BackgroundDte root,
            Map<String, BackgroundDte> byIdentity,
            Map<String, ResolvedBackgroundDte> resolved,
            Set<String> visiting) {
        String id = identity(root.name(), root.source());
        ResolvedBackgroundDte existing = resolved.get(id);
        if (existing != null) {
            return existing;
        }
        if (!visiting.add(id)) {
            throw new IllegalStateException("Background copy cycle detected at " + id);
        }
        BackgroundCopyDte copy = root.copy();
        if (copy == null) {
            ResolvedBackgroundDte result = new ResolvedBackgroundDte(root, root, entries(root.entries()), List.of());
            resolved.put(id, result);
            visiting.remove(id);
            return result;
        }
        String parentIdentity = identity(copy.name(), copy.source());
        BackgroundDte parentRoot = byIdentity.get(parentIdentity);
        if (parentRoot == null) {
            throw new IllegalStateException("Background copy parent is missing: " + parentIdentity);
        }
        ResolvedBackgroundDte parent = resolve(parentRoot, byIdentity, resolved, visiting);
        BackgroundDte effective = merge(root, parent.effective());
        List<EntryDte> effectiveEntries = new ArrayList<>(parent.entries());
        if (copy.modification() != null) {
            for (BackgroundEntryModificationDte operation : copy.modification().operations()) {
                apply(List.of(operation), effectiveEntries, root.name());
            }
        }
        if (root.entries() != null) {
            effectiveEntries = new ArrayList<>(root.entries());
        }
        List<BackgroundId> lineage = new ArrayList<>();
        lineage.add(BackgroundMapper.idFor(parentRoot.name(), parentRoot.source()));
        lineage.addAll(parent.copyLineage());
        ResolvedBackgroundDte result = new ResolvedBackgroundDte(root, effective,
                List.copyOf(effectiveEntries), List.copyOf(lineage));
        resolved.put(id, result);
        visiting.remove(id);
        return result;
    }

    private static void apply(List<BackgroundEntryModificationDte> operations,
                              List<EntryDte> entries,
                              String backgroundName) {
        for (BackgroundEntryModificationDte operation : operations) {
            if ("insertArr".equals(operation.mode())) {
                int index = operation.index();
                if (index < 0 || index > entries.size()) {
                    throw new IllegalStateException("Background insertArr index is out of range for " + backgroundName);
                }
                entries.addAll(index, operation.items());
                continue;
            }
            int index = operation.index() == null
                    ? findNamedEntry(entries, operation.replaceName()) : operation.index();
            if (index < 0 || index >= entries.size()) {
                throw new IllegalStateException("Background replaceArr target is missing for " + backgroundName);
            }
            entries.remove(index);
            entries.addAll(index, operation.items());
        }
    }

    private static int findNamedEntry(List<EntryDte> entries, String name) {
        for (int index = 0; index < entries.size(); index++) {
            EntryDte entry = entries.get(index);
            if (entry instanceof NamedEntryDte named && name.equals(named.name())) {
                return index;
            }
        }
        return -1;
    }

    private static BackgroundDte merge(BackgroundDte child, BackgroundDte parent) {
        return new BackgroundDte(
                child.name(), child.source(), child.page(), choose(child.edition(), parent.edition()),
                choose(child.ability(), parent.ability()),
                choose(child.skillProficiencies(), parent.skillProficiencies()),
                choose(child.toolProficiencies(), parent.toolProficiencies()),
                choose(child.languageProficiencies(), parent.languageProficiencies()),
                choose(child.feats(), parent.feats()),
                choose(child.startingEquipment(), parent.startingEquipment()),
                choose(child.skillToolLanguageProficiencies(), parent.skillToolLanguageProficiencies()),
                choose(child.prerequisite(), parent.prerequisite()), choose(child.additionalSpells(), parent.additionalSpells()),
                choose(child.entries(), parent.entries()),
                choose(child.reprintedAs(), parent.reprintedAs()),
                choose(child.additionalSources(), parent.additionalSources()),
                choose(child.otherSources(), parent.otherSources()),
                choose(child.fromFeature(), parent.fromFeature()),
                choose(child.hasFluff(), parent.hasFluff()),
                choose(child.hasFluffImages(), parent.hasFluffImages()),
                choose(child.srd(), parent.srd()),
                choose(child.srd52(), parent.srd52()), choose(child.basicRules(), parent.basicRules()),
                choose(child.basicRules2024(), parent.basicRules2024()), null);
    }

    private static <T> T choose(T child, T parent) {
        return child == null ? parent : child;
    }

    private static List<EntryDte> entries(List<EntryDte> values) {
        return values == null ? List.of() : List.copyOf(values);
    }

    private static String identity(String name, String source) {
        if (name == null || source == null) {
            throw new IllegalStateException("Background identity requires name and source");
        }
        return name.trim().toLowerCase(java.util.Locale.ROOT) + "\u0000"
                + source.trim().toLowerCase(java.util.Locale.ROOT);
    }
}
