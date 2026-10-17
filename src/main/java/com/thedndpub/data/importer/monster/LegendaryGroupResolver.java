package com.thedndpub.data.importer.monster;

import com.thedndpub.data.dto.monster.support.LegendaryGroupAuthoredRecord;
import com.thedndpub.data.dto.monster.support.LegendaryGroupAuthoredTemplate;
import com.thedndpub.data.dto.monster.support.LegendaryGroupBodyEntry;
import com.thedndpub.data.dto.monster.support.LegendaryGroupDefinition;
import com.thedndpub.data.dto.monster.support.LegendaryGroupId;
import com.thedndpub.data.dto.monster.support.LegendaryGroupInputs;
import com.thedndpub.data.dto.monster.support.LegendaryGroupProvenance;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.legendarygroup.LegendaryGroupArrayMode;
import com.thedndpub.data.dte.legendarygroup.LegendaryGroupArrayModificationDte;
import com.thedndpub.data.mechanics.syntax.SyntaxSourceOwner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/** Resolves the observed support-body append/prepend dialect without invoking Monster transformations. */
public final class LegendaryGroupResolver {
    public Map<LegendaryGroupId, LegendaryGroupDefinition> resolve(LegendaryGroupInputs inputs) {
        Map<LegendaryGroupId, LegendaryGroupAuthoredRecord> authored = new TreeMap<>();
        for (var record : inputs.groups()) {
            if (authored.putIfAbsent(record.id(), record) != null) {
                throw new IllegalArgumentException("Duplicate authored legendary group: " + record.id());
            }
        }
        Map<LegendaryGroupId, LegendaryGroupAuthoredTemplate> templates = new TreeMap<>();
        for (var template : inputs.templates()) {
            if (templates.putIfAbsent(template.id(), template) != null) {
                throw new IllegalArgumentException("Duplicate legendary-group template: " + template.id());
            }
        }
        Map<LegendaryGroupId, LegendaryGroupDefinition> resolved = new TreeMap<>();
        for (var id : authored.keySet()) {
            resolveOriginal(id, authored, resolved, new HashSet<>());
        }

        // Only explicitly authored declarations generate versions; inherited bodies do not inherit declarations.
        for (var record : authored.values()) {
            var versions = record.declaration().versions();
            if (versions == null) {
                continue;
            }
            var parent = resolved.get(record.id());
            for (int index = 0; index < versions.size(); index++) {
                var version = versions.get(index);
                var id = LegendaryGroupId.from(version.name(), version.source());
                var lair = parent.lairActions();
                var regional = parent.regionalEffects();
                List<LegendaryGroupId> appliedTemplates = new ArrayList<>();
                for (var reference : version.templates()) {
                    var templateId = LegendaryGroupId.from(reference.name(), reference.source());
                    var template = templates.get(templateId);
                    if (template == null) {
                        throw new IllegalArgumentException(
                                "Missing legendary-group template " + templateId + " for " + id);
                    }
                    var modifications = template.declaration().apply().modifications();
                    lair = apply(lair, modifications.lairActions(), template.owner(),
                            template.owner().jsonPointer() + "/apply/_mod/lairActions");
                    regional = apply(regional, modifications.regionalEffects(), template.owner(),
                            template.owner().jsonPointer() + "/apply/_mod/regionalEffects");
                    appliedTemplates.add(templateId);
                }
                var provenance = new LegendaryGroupProvenance(
                        record.owner(), record.owner().jsonPointer() + "/_versions/" + index,
                        parent.provenance().copyLineage(), parent.id(), appliedTemplates);
                var expanded = new LegendaryGroupDefinition(
                        id, new SourceReference(id.source(), null), null,
                        lair, regional, parent.mythicEncounter(), provenance);
                if (resolved.putIfAbsent(id, expanded) != null) {
                    throw new IllegalArgumentException("Duplicate generated legendary group: " + id);
                }
            }
        }
        return Collections.unmodifiableMap(new TreeMap<>(resolved));
    }

    private LegendaryGroupDefinition resolveOriginal(
            LegendaryGroupId id,
            Map<LegendaryGroupId, LegendaryGroupAuthoredRecord> authored,
            Map<LegendaryGroupId, LegendaryGroupDefinition> resolved,
            Set<LegendaryGroupId> visiting) {
        if (resolved.containsKey(id)) {
            return resolved.get(id);
        }
        if (!visiting.add(id)) {
            throw new IllegalArgumentException("Legendary-group copy cycle at " + id);
        }
        var record = authored.get(id);
        if (record == null) {
            throw new IllegalArgumentException("Missing legendary-group copy parent: " + id);
        }
        var source = record.declaration();
        var owner = record.owner();
        var lair = entries(source.lairActions(), owner, owner.jsonPointer() + "/lairActions");
        var regional = entries(source.regionalEffects(), owner, owner.jsonPointer() + "/regionalEffects");
        var mythic = entries(source.mythicEncounter(), owner, owner.jsonPointer() + "/mythicEncounter");
        List<LegendaryGroupId> copyLineage = new ArrayList<>();
        if (source.copy() != null) {
            var copy = source.copy();
            var parentId = LegendaryGroupId.from(copy.name(), copy.source());
            var parent = resolveOriginal(parentId, authored, resolved, visiting);
            copyLineage.add(parentId);
            copyLineage.addAll(parent.provenance().copyLineage());
            if (lair == null) {
                lair = parent.lairActions();
            }
            if (regional == null) {
                regional = parent.regionalEffects();
            }
            if (mythic == null) {
                mythic = parent.mythicEncounter();
            }
            if (copy.modifications() != null) {
                lair = apply(lair, copy.modifications().lairActions(), owner,
                        owner.jsonPointer() + "/_copy/_mod/lairActions");
                regional = apply(regional, copy.modifications().regionalEffects(), owner,
                        owner.jsonPointer() + "/_copy/_mod/regionalEffects");
            }
        }
        var additional = source.additionalSources() == null ? null : source.additionalSources().stream()
                .map(reference -> new SourceReference(reference.source(), reference.page()))
                .toList();
        var result = new LegendaryGroupDefinition(id, new SourceReference(id.source(), source.page()),
                additional, lair, regional, mythic,
                new LegendaryGroupProvenance(owner, owner.jsonPointer(), copyLineage, null, List.of()));
        visiting.remove(id);
        resolved.put(id, result);
        return result;
    }

    private static List<LegendaryGroupBodyEntry> apply(
            List<LegendaryGroupBodyEntry> inherited,
            LegendaryGroupArrayModificationDte modification,
            SyntaxSourceOwner owner,
            String pointer) {
        if (modification == null) {
            return inherited;
        }
        var added = entries(modification.items(), owner, pointer + "/items");
        List<LegendaryGroupBodyEntry> result = new ArrayList<>();
        if (modification.mode() == LegendaryGroupArrayMode.PREPEND) {
            result.addAll(added);
        }
        if (inherited != null) {
            result.addAll(inherited);
        }
        if (modification.mode() == LegendaryGroupArrayMode.APPEND) {
            result.addAll(added);
        }
        return List.copyOf(result);
    }

    private static List<LegendaryGroupBodyEntry> entries(
            List<EntryDte> entries,
            SyntaxSourceOwner owner,
            String pointer) {
        if (entries == null) {
            return null;
        }
        List<LegendaryGroupBodyEntry> result = new ArrayList<>();
        for (int index = 0; index < entries.size(); index++) {
            result.add(new LegendaryGroupBodyEntry(entries.get(index), owner, pointer + "/" + index));
        }
        return List.copyOf(result);
    }
}
