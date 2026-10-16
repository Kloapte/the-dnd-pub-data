package com.thedndpub.data.dto.race;

import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.common.entry.EntryTableCellDte;
import com.thedndpub.data.dte.common.entry.InsetEntryDte;
import com.thedndpub.data.dte.common.entry.ListEntryDte;
import com.thedndpub.data.dte.common.entry.ListItemEntryDte;
import com.thedndpub.data.dte.common.entry.NumericTableCellDte;
import com.thedndpub.data.dte.common.entry.NamedEntryDte;
import com.thedndpub.data.dte.common.entry.QuoteEntryDte;
import com.thedndpub.data.dte.common.entry.RollTableCellDte;
import com.thedndpub.data.dte.common.entry.SectionEntryDte;
import com.thedndpub.data.dte.common.entry.StructuredTableCellDte;
import com.thedndpub.data.dte.common.entry.TableEntryDte;
import com.thedndpub.data.dte.common.entry.TextEntryDte;
import com.thedndpub.data.dte.common.entry.TextTableCellDte;
import com.thedndpub.data.dte.race.RaceDte;
import com.thedndpub.data.dte.race.RaceEntryModificationDte;
import com.thedndpub.data.dte.race.RaceModificationDte;
import com.thedndpub.data.dte.race.RaceVersionDte;
import com.thedndpub.data.dte.race.RaceVersionImplementationDte;
import com.thedndpub.data.dte.race.RacesFileDte;
import com.thedndpub.data.dte.race.SubraceDte;
import com.thedndpub.data.dte.feat.FeatNamedResistanceDte;
import com.thedndpub.data.services.SourceRegistry;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Resolves source identities and expands direct and implementation-backed race versions. */
public final class RaceDefinitionNormalizer {
    public List<RaceDefinition> normalize(RacesFileDte source, SourceRegistry registry) {
        if (source.race().isEmpty() && source.subrace().isEmpty()) {
            throw new IllegalStateException("Race source has no race or subrace records");
        }
        validateSourceReferences(source, registry);
        validateRootIdentities(source);
        validateCopyParents(source);

        List<RaceDefinition> definitions = new ArrayList<>();
        Map<String, RaceDefinition> identities = new TreeMap<>();
        Set<String> ids = new HashSet<>();
        for (RaceDte race : source.race()) {
            addRace(definitions, identities, ids, race, source, registry);
            for (RaceVersionDte version : safe(race.versions())) {
                expandRaceVersion(definitions, identities, ids, race, version, source, registry);
            }
        }
        for (SubraceDte subrace : source.subrace()) {
            if (subrace.name() == null) {
                for (RaceVersionDte version : safe(subrace.versions())) {
                    expandSubraceVersion(definitions, identities, ids, subrace, version, source, registry);
                }
                continue;
            }
            addSubrace(definitions, identities, ids, subrace, null, null, null, source, registry);
            for (RaceVersionDte version : safe(subrace.versions())) {
                expandSubraceVersion(definitions, identities, ids, subrace, version, source, registry);
            }
        }
        return List.copyOf(definitions);
    }

    private static void expandRaceVersion(
            List<RaceDefinition> definitions,
            Map<String, RaceDefinition> identities,
            Set<String> ids,
            RaceDte root,
            RaceVersionDte version,
            RacesFileDte sourceFile,
            SourceRegistry registry) {
        if (version.abstractTemplate() != null) {
            if (version.implementations() == null || version.implementations().isEmpty()) {
                throw new IllegalStateException("Race version template has no implementations: "
                        + root.name() + "|" + root.source());
            }
            for (RaceVersionImplementationDte implementation : version.implementations()) {
                String name = substitute(version.abstractTemplate().name(), implementation.variables());
                String source = first(version.abstractTemplate().source(), root.source());
                addRaceVersion(definitions, identities, ids, root, name, source,
                        new RaceSourceData(root, null, null, version, implementation), sourceFile, registry);
            }
            return;
        }
        String name = first(version.name(), root.name());
        String source = first(version.source(), root.source());
        addRaceVersion(definitions, identities, ids, root, name, source,
                new RaceSourceData(root, null, version, null, null), sourceFile, registry);
    }

    private static void expandSubraceVersion(
            List<RaceDefinition> definitions,
            Map<String, RaceDefinition> identities,
            Set<String> ids,
            SubraceDte root,
            RaceVersionDte version,
            RacesFileDte sourceFile,
            SourceRegistry registry) {
        if (version.abstractTemplate() != null) {
            if (version.implementations() == null || version.implementations().isEmpty()) {
                throw new IllegalStateException("Subrace version template has no implementations: "
                        + root.name() + "|" + root.source());
            }
            for (RaceVersionImplementationDte implementation : version.implementations()) {
                String name = substitute(version.abstractTemplate().name(), implementation.variables());
                String source = first(version.abstractTemplate().source(), root.source());
                addSubrace(definitions, identities, ids, root, name, source,
                        new RaceSourceData(null, root, null, version, implementation), sourceFile, registry);
            }
            return;
        }
        String name = first(version.name(), root.name());
        String source = first(version.source(), root.source());
        addSubrace(definitions, identities, ids, root, name, source,
                new RaceSourceData(null, root, version, null, null), sourceFile, registry);
    }

    private static void addRace(
            List<RaceDefinition> definitions,
            Map<String, RaceDefinition> identities,
            Set<String> ids,
            RaceDte race,
            RacesFileDte sourceFile,
            SourceRegistry registry) {
        RaceId id = RaceIdentityFactory.race(race.name(), race.source());
        List<EntryDte> entries = effectiveRaceEntries(race, sourceFile);
        RaceMechanics mechanics = mechanicsForRace(race, sourceFile, new HashSet<>());
        RaceDefinition definition = new RaceDefinition(id, race.name(), source(race.source(), race.page(), registry),
                RaceDefinitionKind.RACE, null,
                new RaceSourceData(race, null, null, null, null),
                mechanics,
                entries,
                traitNames(entries),
                new RaceImportProvenance(List.of(), false, false, copyParentId(race)));
        add(definitions, identities, ids, definition);
    }

    private static void addRaceVersion(
            List<RaceDefinition> definitions,
            Map<String, RaceDefinition> identities,
            Set<String> ids,
            RaceDte root,
            String name,
            String source,
            RaceSourceData sourceData,
            RacesFileDte sourceFile,
            SourceRegistry registry) {
        requireNameSource(name, source);
        RaceId id = RaceIdentityFactory.race(name, source);
        List<EntryDte> entries = applyModifications(effectiveRaceEntries(root, sourceFile), sourceData);
        RaceMechanics mechanics = applyVersionMechanics(
                mechanicsForRace(root, sourceFile, new HashSet<>()),
                sourceData.directVersion(),
                sourceData.implementation());
        RaceDefinition definition = new RaceDefinition(id, name, source(source, root.page(), registry),
                RaceDefinitionKind.RACE, null, sourceData, mechanics,
                entries,
                traitNames(entries),
                new RaceImportProvenance(List.of(), true, sourceData.implementation() != null,
                        RaceIdentityFactory.race(root.name(), root.source()).value()));
        add(definitions, identities, ids, definition);
    }

    private static void addSubrace(
            List<RaceDefinition> definitions,
            Map<String, RaceDefinition> identities,
            Set<String> ids,
            SubraceDte root,
            String versionName,
            String versionSource,
            RaceSourceData versionData,
            RacesFileDte sourceFile,
            SourceRegistry registry) {
        String name = versionName == null ? root.name() : versionName;
        String source = versionSource == null ? root.source() : versionSource;
        requireNameSource(name, source);
        String parentName = root.raceName();
        String parentSource = root.raceSource();
        if ((parentName == null || parentSource == null) && root.copy() != null) {
            parentName = root.copy().raceName();
            parentSource = root.copy().raceSource();
        }
        if (parentName == null || parentSource == null) {
            throw new IllegalStateException("Named subrace has no explicit parent: " + name + "|" + source);
        }
        RaceId id = RaceIdentityFactory.subrace(name, source, parentName, parentSource);
        RaceId parentId = RaceIdentityFactory.race(parentName, parentSource);
        RaceSourceData data = versionData == null
                ? new RaceSourceData(null, root, null, null, null)
                : versionData;
        List<EntryDte> entries = versionData == null
                ? effectiveSubraceEntries(root, sourceFile)
                : applyModifications(subraceVersionEntries(root, sourceFile, versionData), versionData);
        boolean inheritsParentForVersion = versionData != null
                && usesExplicitParentVersionOperation(root, sourceFile, versionData);
        RaceMechanics mechanics = versionData == null
                ? mechanicsForSubrace(root)
                : applyVersionMechanics(inheritsParentForVersion
                                ? mergeMechanics(mechanicsForRace(parentRace(parentName, parentSource, sourceFile),
                                                sourceFile, new HashSet<>()), mechanicsForSubrace(root))
                                : mechanicsForSubrace(root),
                        versionData.directVersion(), versionData.implementation());
        RaceDefinition definition = new RaceDefinition(id, name, nameSource(source, root.page(), registry),
                RaceDefinitionKind.SUBRACE, parentId, data, mechanics, entries, traitNames(entries),
                new RaceImportProvenance(List.of(), versionData != null,
                        versionData != null && versionData.implementation() != null,
                        versionData == null && root.copy() != null
                                ? copySubraceParentId(root, sourceFile)
                                : parentId.value()));
        add(definitions, identities, ids, definition);
    }

    private static SourceReference nameSource(String source, Integer page, SourceRegistry registry) {
        return source(source, page, registry);
    }

    private static RaceMechanics mechanicsForRace(
            RaceDte race,
            RacesFileDte source,
            Set<String> visiting) {
        String identity = identity(race.name(), race.source());
        if (!visiting.add(identity)) {
            throw new IllegalStateException("Race copy cycle detected at " + race.name() + "|" + race.source());
        }
        RaceMechanics local = new RaceMechanics(
                race.size(), race.speed(), race.ability(), race.traitTags(), race.languageProficiencies(),
                race.age(), race.lineage(), race.additionalSpells(), race.darkvision(), race.blindsight(),
                race.resist(), null, race.heightAndWeight(), race.edition(), race.creatureTypes(), race.sizeEntry(),
                race.skillProficiencies(), race.creatureTypeTags(), race.toolProficiencies(),
                race.weaponProficiencies(), race.armorProficiencies(), List.of(),
                race.feats(), race.conditionImmune(), race.immune(), race.vulnerable(), race.alias(), Map.of());
        if (race.copy() == null) {
            visiting.remove(identity);
            return local;
        }
        RaceDte parent = source.race().stream()
                .filter(candidate -> identity(candidate.name(), candidate.source())
                        .equals(identity(race.copy().name(), race.copy().source())))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Missing race copy parent: "
                        + race.copy().name() + "|" + race.copy().source()));
        RaceMechanics inherited = mechanicsForRace(parent, source, visiting);
        visiting.remove(identity);
        return mergeMechanics(inherited, local);
    }

    private static RaceMechanics mechanicsForSubrace(SubraceDte subrace) {
        return new RaceMechanics(
                null, subrace.speed(), subrace.ability(), subrace.traitTags(), subrace.languageProficiencies(),
                subrace.age(), null, subrace.additionalSpells(), subrace.darkvision(), null, subrace.resist(), null,
                subrace.heightAndWeight(), null, null, null, subrace.skillProficiencies(), null,
                subrace.toolProficiencies(), subrace.weaponProficiencies(), subrace.armorProficiencies(),
                subrace.skillToolLanguageProficiencies(), subrace.feats(), null, null, null, subrace.alias(),
                subrace.overwrite());
    }

    private static RaceMechanics applyVersionMechanics(
            RaceMechanics base,
            RaceVersionDte version,
            RaceVersionImplementationDte implementation) {
        if (version == null && implementation == null) {
            return base;
        }
        List<String> resistances = implementation != null && implementation.resist() != null
                ? implementation.resist()
                : version == null ? null : version.resist();
        List<com.thedndpub.data.dte.feat.FeatResistanceDte> namedResistances = resistances == null
                ? null
                : resistances.stream()
                        .map(value -> (com.thedndpub.data.dte.feat.FeatResistanceDte)
                                new FeatNamedResistanceDte(value))
                        .toList();
        Integer darkvision = implementation != null && implementation.darkvision() != null
                ? implementation.darkvision()
                : version == null ? null : version.darkvision();
        var speed = implementation != null && implementation.speed() != null
                ? implementation.speed()
                : version == null ? null : version.speed();
        var traitTags = implementation != null && implementation.traitTags() != null
                ? implementation.traitTags()
                : version == null ? null : version.traitTags();
        var skills = implementation != null && implementation.skillProficiencies() != null
                ? implementation.skillProficiencies()
                : version == null ? null : version.skillProficiencies();
        var spells = version == null ? null : version.additionalSpells();
        var weapons = version == null ? null : version.weaponProficiencies();
        Map<String, Boolean> overwrite = version == null || version.overwrite() == null
                ? Map.of() : version.overwrite();
        if (Boolean.TRUE.equals(overwrite.get("skillProficiencies")) && skills == null) {
            skills = List.of();
        }
        return new RaceMechanics(
                base.size(), choose(speed, base.speed()), base.ability(), choose(traitTags, base.traitTags()),
                base.languageProficiencies(), base.age(), base.lineage(), choose(spells, base.additionalSpells()),
                choose(darkvision, base.darkvision()), base.blindsight(),
                namedResistances == null ? base.resistances() : namedResistances,
                resistances == null ? base.implementationResistances() : resistances,
                base.heightAndWeight(), base.edition(), base.creatureTypes(), base.sizeEntry(),
                choose(skills, base.skillProficiencies()), base.creatureTypeTags(), base.toolProficiencies(),
                choose(weapons, base.weaponProficiencies()), base.armorProficiencies(),
                base.skillToolLanguageProficiencies(), base.featGrants(), base.conditionImmunities(),
                base.immunities(), base.vulnerabilities(), base.aliases(), overwrite);
    }

    private static RaceMechanics mergeMechanics(RaceMechanics parent, RaceMechanics local) {
        return new RaceMechanics(
                choose(local.size(), parent.size()), choose(local.speed(), parent.speed()),
                choose(local.ability(), parent.ability()), choose(local.traitTags(), parent.traitTags()),
                choose(local.languageProficiencies(), parent.languageProficiencies()),
                choose(local.age(), parent.age()), choose(local.lineage(), parent.lineage()),
                choose(local.additionalSpells(), parent.additionalSpells()),
                choose(local.darkvision(), parent.darkvision()), choose(local.blindsight(), parent.blindsight()),
                choose(local.resistances(), parent.resistances()),
                choose(local.implementationResistances(), parent.implementationResistances()),
                choose(local.heightAndWeight(), parent.heightAndWeight()), choose(local.edition(), parent.edition()),
                choose(local.creatureTypes(), parent.creatureTypes()), choose(local.sizeEntry(), parent.sizeEntry()),
                choose(local.skillProficiencies(), parent.skillProficiencies()),
                choose(local.creatureTypeTags(), parent.creatureTypeTags()),
                choose(local.toolProficiencies(), parent.toolProficiencies()),
                choose(local.weaponProficiencies(), parent.weaponProficiencies()),
                choose(local.armorProficiencies(), parent.armorProficiencies()),
                choose(local.skillToolLanguageProficiencies(), parent.skillToolLanguageProficiencies()),
                choose(local.featGrants(), parent.featGrants()),
                choose(local.conditionImmunities(), parent.conditionImmunities()),
                choose(local.immunities(), parent.immunities()),
                choose(local.vulnerabilities(), parent.vulnerabilities()),
                choose(local.aliases(), parent.aliases()), local.overwrite());
    }

    private static <T> T choose(T preferred, T fallback) {
        return preferred == null ? fallback : preferred;
    }

    private static <T> List<T> choose(List<T> preferred, List<T> fallback) {
        return preferred == null || preferred.isEmpty() ? fallback : preferred;
    }

    private static RaceDte parentRace(String name, String source, RacesFileDte file) {
        return file.race().stream()
                .filter(candidate -> identity(candidate.name(), candidate.source()).equals(identity(name, source)))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Missing parent race: " + name + "|" + source));
    }

    private static void add(
            List<RaceDefinition> definitions,
            Map<String, RaceDefinition> identities,
            Set<String> ids,
            RaceDefinition definition) {
        if (!ids.add(definition.id().value())) {
            throw new IllegalStateException("Duplicate race definition ID: " + definition.id().value());
        }
        String identity = identity(definition.name(), definition.source().identity().code())
                + "\u0000" + definition.kind() + "\u0000" + definition.parentId();
        if (identities.putIfAbsent(identity, definition) != null) {
            throw new IllegalStateException("Duplicate race name/source/kind/parent identity: " + identity);
        }
        definitions.add(definition);
    }

    private static void validateRootIdentities(RacesFileDte source) {
        Set<String> identities = new HashSet<>();
        for (RaceDte race : source.race()) {
            requireNameSource(race.name(), race.source());
            if (!identities.add("RACE\u0000" + identity(race.name(), race.source()))) {
                throw new IllegalStateException("Duplicate race root identity: " + race.name() + "|" + race.source());
            }
        }
        for (SubraceDte subrace : source.subrace()) {
            if (subrace.name() == null) {
                continue;
            }
            requireNameSource(subrace.name(), subrace.source());
            String key = "SUBRACE\u0000" + identity(subrace.name(), subrace.source()) + "\u0000"
                    + identity(subrace.raceName(), subrace.raceSource());
            if (!identities.add(key)) {
                throw new IllegalStateException("Duplicate subrace root identity: " + key);
            }
        }
    }

    private static void validateCopyParents(RacesFileDte source) {
        Set<String> races = new HashSet<>();
        Set<String> subraces = new HashSet<>();
        for (RaceDte race : source.race()) {
            races.add(identity(race.name(), race.source()));
        }
        for (SubraceDte subrace : source.subrace()) {
            if (subrace.name() != null) {
                subraces.add(identity(subrace.name(), subrace.source()));
            }
        }
        for (RaceDte race : source.race()) {
            if (race.copy() != null && !races.contains(identity(race.copy().name(), race.copy().source()))) {
                throw new IllegalStateException("Missing race copy parent: " + race.copy().name()
                        + "|" + race.copy().source());
            }
        }
        for (SubraceDte subrace : source.subrace()) {
            if (subrace.copy() == null) {
                continue;
            }
            String parent = identity(subrace.copy().name(), subrace.copy().source());
            boolean namelessVariantTemplate = source.subrace().stream()
                    .anyMatch(candidate -> candidate.name() == null
                            && identity(candidate.source(), subrace.copy().source()).equals("phb\u0000phb")
                            && candidate.raceName() != null
                            && identity(candidate.raceName(), candidate.raceSource())
                                    .equals(identity(subrace.copy().raceName(), subrace.copy().raceSource())));
            if (!subraces.contains(parent) && !namelessVariantTemplate) {
                throw new IllegalStateException("Missing subrace copy parent: " + subrace.copy().name()
                        + "|" + subrace.copy().source());
            }
        }
        for (SubraceDte subrace : source.subrace()) {
            if (subrace.raceName() != null
                    && !races.contains(identity(subrace.raceName(), subrace.raceSource()))) {
                throw new IllegalStateException("Missing parent race: " + subrace.raceName()
                        + "|" + subrace.raceSource());
            }
        }
    }

    private static void validateSourceReferences(RacesFileDte source, SourceRegistry registry) {
        for (RaceDte race : source.race()) {
            validateSource(race.source(), registry);
            validateReferences(race.otherSources(), registry);
            validateReferences(race.additionalSources(), registry);
            validateReprints(race.reprintedAs(), registry);
            if (race.copy() != null) {
                validateSource(race.copy().source(), registry);
            }
        }
        for (SubraceDte subrace : source.subrace()) {
            validateSource(subrace.source(), registry);
            validateSource(subrace.raceSource(), registry);
            validateReferences(subrace.otherSources(), registry);
            validateReprints(subrace.reprintedAs(), registry);
            if (subrace.copy() != null) {
                validateSource(subrace.copy().source(), registry);
                validateSource(subrace.copy().raceSource(), registry);
            }
        }
    }

    private static void validateReferences(
            List<com.thedndpub.data.dte.race.RaceSourceReferenceDte> references,
            SourceRegistry registry) {
        if (references == null) {
            return;
        }
        for (var reference : references) {
            if (reference != null) {
                validateSource(reference.source(), registry);
            }
        }
    }

    private static void validateReprints(
            List<com.thedndpub.data.dte.race.RaceReprintDte> references,
            SourceRegistry registry) {
        if (references == null) {
            return;
        }
        for (var reference : references) {
            if (reference == null || reference.uid() == null) {
                throw new IllegalStateException("Race reprint reference has no UID");
            }
            int sourceSeparator = reference.uid().lastIndexOf('|');
            if (sourceSeparator <= 0 || sourceSeparator == reference.uid().length() - 1) {
                continue;
            }
            validateSource(reference.uid().substring(sourceSeparator + 1), registry);
        }
    }

    private static void validateSource(String source, SourceRegistry registry) {
        if (source != null && registry.find(source) == null) {
            throw new IllegalStateException("Unknown race source code: " + source);
        }
    }

    private static List<String> traitNames(List<EntryDte> entries) {
        if (entries == null) {
            return List.of();
        }
        List<String> names = new ArrayList<>();
        for (EntryDte entry : entries) {
            collectTraitNames(entry, names);
        }
        return List.copyOf(names);
    }

    private static void collectTraitNames(EntryDte entry, List<String> names) {
        if (entry instanceof NamedEntryDte named) {
            addName(names, named.name());
            traitNames(named.entries()).forEach(names::add);
        } else if (entry instanceof ListEntryDte list) {
            traitNames(list.items()).forEach(names::add);
        } else if (entry instanceof ListItemEntryDte item) {
            addName(names, item.name());
            traitNames(item.entries()).forEach(names::add);
        } else if (entry instanceof InsetEntryDte inset) {
            addName(names, inset.name());
            traitNames(inset.entries()).forEach(names::add);
        } else if (entry instanceof QuoteEntryDte quote) {
            traitNames(quote.entries()).forEach(names::add);
        } else if (entry instanceof SectionEntryDte section) {
            addName(names, section.name());
            traitNames(section.entries()).forEach(names::add);
        } else if (entry instanceof TableEntryDte table) {
            addName(names, table.caption());
        }
    }

    private static void addName(List<String> names, String name) {
        if (name != null && !name.isBlank()) {
            names.add(name);
        }
    }

    private static List<EntryDte> effectiveRaceEntries(RaceDte race, RacesFileDte source) {
        return effectiveRaceEntries(race, source, new HashSet<>());
    }

    private static List<EntryDte> effectiveRaceEntries(
            RaceDte race,
            RacesFileDte source,
            Set<String> visiting) {
        String currentIdentity = identity(race.name(), race.source());
        if (!visiting.add(currentIdentity)) {
            throw new IllegalStateException("Race copy cycle detected at " + race.name() + "|" + race.source());
        }
        List<EntryDte> local = entries(race.entries());
        if (race.copy() == null) {
            visiting.remove(currentIdentity);
            return local;
        }
        RaceDte parent = source.race().stream()
                .filter(candidate -> identity(candidate.name(), candidate.source())
                        .equals(identity(race.copy().name(), race.copy().source())))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Missing race copy parent: "
                        + race.copy().name() + "|" + race.copy().source()));
        List<EntryDte> inherited = effectiveRaceEntries(parent, source, visiting);
        List<EntryDte> result = local.isEmpty() ? inherited : local;
        if (race.copy().modification() == null) {
            visiting.remove(currentIdentity);
            return result;
        }
        List<EntryDte> modified = applyOperations(result, race.copy().modification());
        visiting.remove(currentIdentity);
        return modified;
    }

    private static List<EntryDte> effectiveSubraceEntries(SubraceDte subrace, RacesFileDte source) {
        return effectiveSubraceEntries(subrace, source, new HashSet<>());
    }

    private static List<EntryDte> effectiveSubraceEntries(
            SubraceDte subrace,
            RacesFileDte source,
            Set<String> visiting) {
        String currentIdentity = identity(subrace.name(), subrace.source());
        if (subrace.name() != null && !visiting.add(currentIdentity)) {
            throw new IllegalStateException("Subrace copy cycle detected at " + subrace.name() + "|" + subrace.source());
        }
        List<EntryDte> local = entries(subrace.entries());
        if (subrace.name() == null) {
            return raceParentEntries(subrace.raceName(), subrace.raceSource(), source);
        }
        if (subrace.copy() == null) {
            visiting.remove(currentIdentity);
            return local;
        }
        SubraceDte parent = source.subrace().stream()
                .filter(candidate -> candidate.name() != null
                        && identity(candidate.name(), candidate.source())
                        .equals(identity(subrace.copy().name(), subrace.copy().source())))
                .findFirst()
                .orElse(null);
        List<EntryDte> inherited = parent == null ? List.of() : effectiveSubraceEntries(parent, source, visiting);
        List<EntryDte> result = local.isEmpty() ? inherited : local;
        List<EntryDte> modified = subrace.copy().modification() == null
                ? result : applyOperations(result, subrace.copy().modification());
        visiting.remove(currentIdentity);
        return modified;
    }

    private static List<EntryDte> subraceVersionEntries(
            SubraceDte subrace,
            RacesFileDte source,
            RaceSourceData versionData) {
        List<EntryDte> local = effectiveSubraceEntries(subrace, source);
        if (subrace.raceName() == null) {
            return raceParentEntries(subrace.raceName(), subrace.raceSource(), source);
        }
        List<RaceEntryModificationDte> operations = modificationOperations(versionData);
        if (!usesExplicitParentVersionOperation(subrace, source, versionData)) {
            return local;
        }
        List<EntryDte> parentEntries = raceParentEntries(subrace.raceName(), subrace.raceSource(), source);
        List<EntryDte> combined = new ArrayList<>(parentEntries);
        combined.addAll(local);
        return List.copyOf(combined);
    }

    private static boolean usesExplicitParentVersionOperation(
            SubraceDte subrace,
            RacesFileDte source,
            RaceSourceData versionData) {
        if (subrace.name() == null) {
            return true;
        }
        List<EntryDte> local = effectiveSubraceEntries(subrace, source);
        List<EntryDte> parent = raceParentEntries(subrace.raceName(), subrace.raceSource(), source);
        List<String> localNames = topLevelNames(local);
        List<String> parentNames = topLevelNames(parent);
        for (RaceEntryModificationDte operation : modificationOperations(versionData)) {
            if ("replaceArr".equals(operation.mode()) && !localNames.contains(operation.replace())
                    && parentNames.contains(operation.replace())) {
                return true;
            }
            if ("removeArr".equals(operation.mode()) && operation.names().stream()
                    .anyMatch(name -> !localNames.contains(name) && parentNames.contains(name))) {
                return true;
            }
        }
        return false;
    }

    private static List<String> topLevelNames(List<EntryDte> entries) {
        return entries.stream().map(RaceDefinitionNormalizer::entryName)
                .filter(name -> name != null).toList();
    }

    private static List<RaceEntryModificationDte> modificationOperations(RaceSourceData sourceData) {
        List<RaceEntryModificationDte> operations = new ArrayList<>();
        if (sourceData.template() != null && sourceData.template().abstractTemplate() != null
                && sourceData.template().abstractTemplate().modification() != null) {
            operations.addAll(sourceData.template().abstractTemplate().modification().entries());
        }
        if (sourceData.directVersion() != null && sourceData.directVersion().modification() != null) {
            operations.addAll(sourceData.directVersion().modification().entries());
        }
        return List.copyOf(operations);
    }

    private static List<EntryDte> raceParentEntries(String name, String sourceCode, RacesFileDte source) {
        RaceDte parent = source.race().stream()
                .filter(candidate -> identity(candidate.name(), candidate.source()).equals(identity(name, sourceCode)))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Missing parent race: " + name + "|" + sourceCode));
        return effectiveRaceEntries(parent, source);
    }

    private static List<EntryDte> applyModifications(List<EntryDte> entries, RaceSourceData sourceData) {
        List<EntryDte> result = entries;
        if (sourceData.implementation() != null) {
            result = substituteEntries(result, sourceData.implementation().variables());
        }
        RaceModificationDte mod = null;
        if (sourceData.template() != null && sourceData.template().abstractTemplate() != null) {
            mod = sourceData.template().abstractTemplate().modification();
        }
        if (mod != null) {
            if (sourceData.implementation() != null) {
                mod = substituteModification(mod, sourceData.implementation().variables());
            }
            result = applyOperations(result, mod);
        }
        if (sourceData.directVersion() != null && sourceData.directVersion().modification() != null) {
            result = applyOperations(result, sourceData.directVersion().modification());
        }
        for (EntryDte entry : result) {
            if (containsPlaceholder(entry)) {
                throw new IllegalStateException("Unresolved race version placeholder in entry " + entry);
            }
        }
        return result;
    }

    private static RaceModificationDte substituteModification(
            RaceModificationDte modification,
            com.thedndpub.data.dte.race.RaceTemplateVariablesDte variables) {
        List<RaceEntryModificationDte> operations = modification.entries().stream()
                .map(operation -> new RaceEntryModificationDte(
                        operation.mode(),
                        substituteValue(operation.replace(), variables),
                        operation.names().stream().map(name -> substituteValue(name, variables)).toList(),
                        substituteValue(operation.with(), variables),
                        operation.flags(),
                        substituteEntries(operation.items(), variables)))
                .toList();
        return new RaceModificationDte(operations);
    }

    private static String substituteValue(
            String value,
            com.thedndpub.data.dte.race.RaceTemplateVariablesDte variables) {
        if (value == null) {
            return null;
        }
        Map<String, String> replacements = new TreeMap<>();
        replacements.put("color", variables.color());
        replacements.put("damageType", variables.damageType());
        replacements.put("area", variables.area());
        replacements.put("savingThrow", variables.savingThrow());
        String result = value;
        for (Map.Entry<String, String> replacement : replacements.entrySet()) {
            if (replacement.getValue() != null) {
                result = result.replace("{{" + replacement.getKey() + "}}", replacement.getValue());
            }
        }
        if (result.contains("{{")) {
            throw new IllegalStateException("Unresolved race template placeholder: " + result);
        }
        return result;
    }

    private static List<EntryDte> substituteEntries(
            List<EntryDte> entries,
            com.thedndpub.data.dte.race.RaceTemplateVariablesDte variables) {
        List<EntryDte> result = entries;
        Map<String, String> replacements = new TreeMap<>();
        replacements.put("color", variables.color());
        replacements.put("damageType", variables.damageType());
        replacements.put("area", variables.area());
        replacements.put("savingThrow", variables.savingThrow());
        for (Map.Entry<String, String> replacement : replacements.entrySet()) {
            if (replacement.getValue() == null) {
                continue;
            }
            Pattern pattern = Pattern.compile(Pattern.quote("{{" + replacement.getKey() + "}}"));
            result = replaceText(result, pattern, replacement.getValue());
        }
        return result;
    }

    private static List<EntryDte> applyOperations(List<EntryDte> base, RaceModificationDte modification) {
        List<EntryDte> entries = new ArrayList<>(base);
        for (RaceEntryModificationDte operation : modification.entries()) {
            switch (operation.mode()) {
                case "appendArr" -> entries.addAll(operation.items());
                case "prependArr" -> entries.addAll(0, operation.items());
                case "replaceArr" -> {
                    int index = findEntry(entries, operation.replace());
                    if (index < 0) {
                        throw new IllegalStateException("Race replaceArr target not found: " + operation.replace()
                                + " among " + entries.stream().map(RaceDefinitionNormalizer::entryName).toList());
                    }
                    entries.remove(index);
                    entries.addAll(index, operation.items());
                }
                case "removeArr" -> {
                    for (String name : operation.names()) {
                        int index = findEntry(entries, name);
                        if (index < 0) {
                            throw new IllegalStateException("Race removeArr target not found: " + name);
                        }
                        entries.remove(index);
                    }
                }
                case "replaceTxt" -> replaceText(entries, operation);
                default -> throw new IllegalStateException("Unsupported race modification: " + operation.mode());
            }
        }
        return List.copyOf(entries);
    }

    private static int findEntry(List<EntryDte> entries, String name) {
        for (int index = 0; index < entries.size(); index++) {
            EntryDte entry = entries.get(index);
            if (entryName(entry) != null && name.equals(entryName(entry))) {
                return index;
            }
        }
        return -1;
    }

    private static String entryName(EntryDte entry) {
        if (entry instanceof NamedEntryDte named) {
            return named.name();
        }
        if (entry instanceof InsetEntryDte inset) {
            return inset.name();
        }
        if (entry instanceof SectionEntryDte section) {
            return section.name();
        }
        if (entry instanceof ListItemEntryDte item) {
            return item.name();
        }
        return null;
    }

    private static void replaceText(List<EntryDte> entries, RaceEntryModificationDte operation) {
        if (operation.replace() == null || operation.with() == null) {
            throw new IllegalStateException("Race replaceTxt requires replace and with values");
        }
        Pattern pattern = Pattern.compile(Pattern.quote(operation.replace()),
                "i".equalsIgnoreCase(operation.flags()) ? Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE : 0);
        for (int index = 0; index < entries.size(); index++) {
            entries.set(index, replaceText(entries.get(index), pattern, operation.with()));
        }
    }

    private static EntryDte replaceText(EntryDte entry, Pattern pattern, String replacement) {
        if (entry instanceof TextEntryDte text) {
            return new TextEntryDte(replace(text.text(), pattern, replacement));
        }
        if (entry instanceof NamedEntryDte named) {
            return new NamedEntryDte(named.type(), replace(named.name(), pattern, replacement),
                    replace(named.entry(), pattern, replacement), replaceText(named.entries(), pattern, replacement),
                    named.source(), named.page(), named.data());
        }
        if (entry instanceof ListEntryDte list) {
            return new ListEntryDte(list.style(), list.columns(), replaceText(list.items(), pattern, replacement));
        }
        if (entry instanceof ListItemEntryDte item) {
            return new ListItemEntryDte(replace(item.name(), pattern, replacement),
                    replace(item.entry(), pattern, replacement), replaceText(item.entries(), pattern, replacement));
        }
        if (entry instanceof InsetEntryDte inset) {
            return new InsetEntryDte(replace(inset.name(), pattern, replacement),
                    replaceText(inset.entries(), pattern, replacement), inset.source(), inset.page(), inset.data());
        }
        if (entry instanceof QuoteEntryDte quote) {
            return new QuoteEntryDte(replace(quote.by(), pattern, replacement),
                    replaceText(quote.entries(), pattern, replacement));
        }
        if (entry instanceof SectionEntryDte section) {
            return new SectionEntryDte(replace(section.name(), pattern, replacement), section.id(),
                    replaceText(section.entries(), pattern, replacement));
        }
        if (entry instanceof TableEntryDte table) {
            List<List<EntryTableCellDte>> rows = table.rows().stream()
                    .map(row -> row.stream().map(cell -> replaceText(cell, pattern, replacement)).toList())
                    .toList();
            return new TableEntryDte(replace(table.caption(), pattern, replacement),
                    replace(table.colLabels(), pattern, replacement), table.colStyles(), rows, table.source(),
                    table.page(), replace(table.footnotes(), pattern, replacement), table.data(), table.srd52());
        }
        return entry;
    }

    private static EntryTableCellDte replaceText(
            EntryTableCellDte cell, Pattern pattern, String replacement) {
        if (cell instanceof TextTableCellDte text) {
            return new TextTableCellDte(replace(text.text(), pattern, replacement));
        }
        if (cell instanceof StructuredTableCellDte structured) {
            return new StructuredTableCellDte(structured.type(),
                    replace(structured.name(), pattern, replacement),
                    replaceText(structured.entries(), pattern, replacement));
        }
        if (cell instanceof RollTableCellDte roll) {
            return new RollTableCellDte(roll.type(), roll.roll(),
                    replaceText(roll.entry(), pattern, replacement));
        }
        if (cell instanceof NumericTableCellDte) {
            return cell;
        }
        throw new IllegalStateException("Unsupported race table cell: " + cell.getClass().getName());
    }

    private static List<EntryDte> replaceText(List<EntryDte> entries, Pattern pattern, String replacement) {
        if (entries == null) {
            return List.of();
        }
        return entries.stream().map(entry -> replaceText(entry, pattern, replacement)).toList();
    }

    private static String replace(String value, Pattern pattern, String replacement) {
        if (value == null) {
            return null;
        }
        Matcher matcher = pattern.matcher(value);
        return matcher.replaceAll(Matcher.quoteReplacement(replacement));
    }

    private static List<String> replace(List<String> values, Pattern pattern, String replacement) {
        if (values == null) {
            return null;
        }
        return values.stream().map(value -> replace(value, pattern, replacement)).toList();
    }

    private static boolean containsPlaceholder(EntryDte entry) {
        return traitNames(List.of(entry)).stream().anyMatch(name -> name.contains("{{"))
                || entry.toString().contains("{{");
    }

    private static List<EntryDte> entries(List<EntryDte> entries) {
        return entries == null ? List.of() : List.copyOf(entries);
    }

    private static String copyParentId(RaceDte race) {
        if (race.copy() == null) {
            return null;
        }
        return RaceIdentityFactory.race(race.copy().name(), race.copy().source()).value();
    }

    private static String copySubraceParentId(SubraceDte subrace, RacesFileDte source) {
        if (subrace.copy() == null) {
            return null;
        }
        if (subrace.copy().raceName() != null && subrace.copy().raceSource() != null) {
            return RaceIdentityFactory.race(subrace.copy().raceName(), subrace.copy().raceSource()).value();
        }
        return "subrace-copy:" + subrace.copy().source().toLowerCase(Locale.ROOT) + ":"
                + subrace.copy().name().toLowerCase(Locale.ROOT);
    }

    private static SourceReference source(String source, Integer page, SourceRegistry registry) {
        SourceIdentity identity = new SourceIdentity(source);
        if (registry.find(source) == null) {
            throw new IllegalStateException("Unknown race source code: " + source);
        }
        return new SourceReference(identity, page);
    }

    private static String substitute(String template, com.thedndpub.data.dte.race.RaceTemplateVariablesDte values) {
        if (template == null || values == null) {
            throw new IllegalStateException("Template version has no name or variables");
        }
        Map<String, String> replacements = new HashMap<>();
        replacements.put("color", values.color());
        replacements.put("damageType", values.damageType());
        replacements.put("area", values.area());
        replacements.put("savingThrow", values.savingThrow());
        String result = template;
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            if (entry.getValue() != null) {
                result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }
        }
        if (result.contains("{{")) {
            throw new IllegalStateException("Unresolved race version placeholder in name: " + result);
        }
        return result;
    }

    private static String first(String preferred, String fallback) {
        return preferred == null || preferred.isBlank() ? fallback : preferred;
    }

    private static void requireNameSource(String name, String source) {
        if (name == null || name.isBlank() || source == null || source.isBlank()) {
            throw new IllegalStateException("Race definition requires a nonblank name and source");
        }
    }

    private static String identity(String name, String source) {
        if (name == null || source == null) {
            return "(null)\u0000(null)";
        }
        return normalize(name) + "\u0000" + normalize(source);
    }

    private static String normalize(String value) {
        return Normalizer.normalize(value.trim(), Normalizer.Form.NFKC).toLowerCase(Locale.ROOT);
    }

    private static <T> List<T> safe(List<T> values) {
        return values == null ? List.of() : values;
    }
}
