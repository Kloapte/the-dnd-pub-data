package com.thedndpub.data.dte.feat;

import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.common.entry.ListItemEntryDte;
import com.thedndpub.data.dte.common.entry.NamedEntryDte;
import com.thedndpub.data.dte.common.entry.TextEntryDte;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;

/** Applies the concrete Feat version operations observed in the source corpus. */
@Component
public final class FeatVersionExpander {
    public List<ExpandedFeatDte> expand(FeatDte parent) {
        Objects.requireNonNull(parent, "parent");
        if (parent.name() == null || parent.source() == null) {
            throw new IllegalArgumentException("Feat version parent requires name and source");
        }
        List<ExpandedFeatDte> expanded = new ArrayList<>();
        expanded.add(new ExpandedFeatDte(parent, null, null, null, null));
        if (parent.versions() == null) {
            return List.copyOf(expanded);
        }
        for (int index = 0; index < parent.versions().size(); index++) {
            FeatVersionDte version = parent.versions().get(index);
            validateVersion(parent, version, index);
            List<EntryDte> entries = copyEntries(parent.entries());
            applyModifications(parent, version, index, entries);
            FeatDte variant = copyWithVersionOverrides(parent, version, entries);
            expanded.add(new ExpandedFeatDte(variant, parent.name(), parent.source(), index, version));
        }
        return List.copyOf(expanded);
    }

    private static void validateVersion(FeatDte parent, FeatVersionDte version, int index) {
        if (version == null || blank(version.name()) || blank(version.source())) {
            throw error(parent, index, "version must declare nonblank name and source");
        }
        if (version.modification() != null && version.modification().entries() != null) {
            for (int operationIndex = 0; operationIndex < version.modification().entries().size(); operationIndex++) {
                FeatEntryModificationDte operation = version.modification().entries().get(operationIndex);
                if (operation == null || operation.mode() == null) {
                    throw error(parent, index, "entry modification " + operationIndex + " requires a mode");
                }
                if (!operation.mode().equals("replaceArr") && !operation.mode().equals("removeArr")) {
                    throw error(parent, index, "unsupported entry modification mode '" + operation.mode() + "'");
                }
                if (operation.mode().equals("replaceArr")) {
                    if (operation.replace() == null || operation.items() == null) {
                        throw error(parent, index, "replaceArr requires replace and items");
                    }
                    validateItems(parent, index, operation.items());
                } else if (operation.names() == null || operation.names().isEmpty()) {
                    throw error(parent, index, "removeArr requires a nonempty names list");
                }
            }
        }
    }

    private static void validateItems(FeatDte parent, int versionIndex, FeatModificationItemsDte items) {
        if (items instanceof FeatModificationNamedItemsDte named) {
            if (blank(named.name()) || named.entries() == null) {
                throw error(parent, versionIndex, "named replacement requires a name and entries");
            }
        } else if (items instanceof FeatModificationTextItemsDte text) {
            if (text.values() == null || text.values().stream().anyMatch(Objects::isNull)) {
                throw error(parent, versionIndex, "text replacement items must be nonnull strings");
            }
        } else {
            throw error(parent, versionIndex, "unsupported replaceArr payload");
        }
    }

    private static void applyModifications(
            FeatDte parent,
            FeatVersionDte version,
            int versionIndex,
            List<EntryDte> entries) {
        if (version.modification() == null || version.modification().entries() == null) {
            return;
        }
        for (int operationIndex = 0; operationIndex < version.modification().entries().size(); operationIndex++) {
            FeatEntryModificationDte operation = version.modification().entries().get(operationIndex);
            if (operation.mode().equals("replaceArr")) {
                replace(parent, versionIndex, operationIndex, operation, entries);
            } else if (operation.mode().equals("removeArr")) {
                remove(parent, versionIndex, operationIndex, operation.names(), entries);
            }
        }
    }

    private static void replace(
            FeatDte parent,
            int versionIndex,
            int operationIndex,
            FeatEntryModificationDte operation,
            List<EntryDte> entries) {
        int targetIndex = findTarget(parent, versionIndex, operationIndex, operation.replace(), entries);
        List<EntryDte> replacements = replacementEntries(parent, versionIndex, operation.items());
        entries.remove(targetIndex);
        entries.addAll(targetIndex, replacements);
    }

    private static int findTarget(
            FeatDte parent,
            int versionIndex,
            int operationIndex,
            FeatReplaceTargetDte target,
            List<EntryDte> entries) {
        if (target instanceof FeatReplaceIndexDte indexTarget) {
            int index = indexTarget.index();
            if (index < 0 || index >= entries.size()) {
                throw error(parent, versionIndex, "replaceArr operation " + operationIndex
                        + " index is out of range: " + index);
            }
            return index;
        }
        if (target instanceof FeatReplaceNameDte nameTarget) {
            int match = -1;
            for (int index = 0; index < entries.size(); index++) {
                if (entryName(entries.get(index)).equals(nameTarget.name())) {
                    if (match >= 0) {
                        throw error(parent, versionIndex, "replaceArr operation " + operationIndex
                                + " has ambiguous entry name: " + nameTarget.name());
                    }
                    match = index;
                }
            }
            if (match < 0) {
                throw error(parent, versionIndex, "replaceArr operation " + operationIndex
                        + " entry name not found: " + nameTarget.name());
            }
            return match;
        }
        throw error(parent, versionIndex, "replaceArr operation " + operationIndex + " has unsupported selector");
    }

    private static List<EntryDte> replacementEntries(
            FeatDte parent,
            int versionIndex,
            FeatModificationItemsDte items) {
        if (items instanceof FeatModificationNamedItemsDte named) {
            return List.of(new NamedEntryDte("entries", named.name(), null,
                    List.copyOf(named.entries()), null, null));
        }
        if (items instanceof FeatModificationTextItemsDte text) {
            return text.values().stream().map(TextEntryDte::new).map(EntryDte.class::cast).toList();
        }
        throw error(parent, versionIndex, "unsupported replacement payload");
    }

    private static void remove(
            FeatDte parent,
            int versionIndex,
            int operationIndex,
            List<String> names,
            List<EntryDte> entries) {
        for (String name : names) {
            int match = -1;
            for (int index = 0; index < entries.size(); index++) {
                if (entryName(entries.get(index)).equals(name)) {
                    if (match >= 0) {
                        throw error(parent, versionIndex, "removeArr operation " + operationIndex
                                + " has ambiguous entry name: " + name);
                    }
                    match = index;
                }
            }
            if (match < 0) {
                throw error(parent, versionIndex, "removeArr operation " + operationIndex
                        + " entry name not found: " + name);
            }
            entries.remove(match);
        }
    }

    private static String entryName(EntryDte entry) {
        if (entry instanceof NamedEntryDte named) {
            return Objects.toString(named.name(), "");
        }
        if (entry instanceof ListItemEntryDte item) {
            return Objects.toString(item.name(), "");
        }
        return "";
    }

    private static List<EntryDte> copyEntries(List<EntryDte> entries) {
        return entries == null ? new ArrayList<>() : new ArrayList<>(entries);
    }

    private static FeatDte copyWithVersionOverrides(FeatDte parent, FeatVersionDte version, List<EntryDte> entries) {
        return new FeatDte(version.name(), version.source(), parent.page(), parent.category(), parent.prerequisite(),
                parent.ability(), parent.additionalSources(),
                version.additionalSpells() == null ? parent.additionalSpells() : version.additionalSpells(),
                parent.skillProficiencies(), parent.expertise(), parent.languageProficiencies(),
                parent.toolProficiencies(), parent.weaponProficiencies(), parent.armorProficiencies(),
                parent.savingThrowProficiencies(), parent.skillToolLanguageProficiencies(), parent.conditionImmune(),
                parent.immune(), parent.resist(), parent.senses(), parent.bonusSenses(),
                parent.optionalfeatureProgression(), parent.traitTags(), List.copyOf(entries), parent.reprintedAs(),
                parent.repeatable(), parent.repeatableHidden(), parent.srd(), parent.srd52(),
                parent.basicRules2024(), parent.hasFluffImages(), null);
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }

    private static IllegalArgumentException error(FeatDte parent, int versionIndex, String message) {
        return new IllegalArgumentException("Feat '" + parent.name() + "|" + parent.source()
                + "' version[" + versionIndex + "]: " + message);
    }
}
