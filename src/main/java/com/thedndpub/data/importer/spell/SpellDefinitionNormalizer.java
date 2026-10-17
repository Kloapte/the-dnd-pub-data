package com.thedndpub.data.importer.spell;

import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dto.spell.SpellCatalogReference;
import com.thedndpub.data.dto.spell.SpellCastingTime;
import com.thedndpub.data.dto.spell.SpellComponents;
import com.thedndpub.data.dto.spell.SpellConsume;
import com.thedndpub.data.dto.spell.SpellConsumeFlag;
import com.thedndpub.data.dto.spell.SpellConsumeNote;
import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.dto.spell.SpellDistance;
import com.thedndpub.data.dto.spell.SpellDuration;
import com.thedndpub.data.dto.spell.SpellDurationAmount;
import com.thedndpub.data.dto.spell.SpellIdentity;
import com.thedndpub.data.dto.spell.SpellImportMetadata;
import com.thedndpub.data.dto.spell.SpellMaterial;
import com.thedndpub.data.dto.spell.SpellMaterialDetails;
import com.thedndpub.data.dto.spell.SpellMaterialText;
import com.thedndpub.data.dto.spell.SpellMechanics;
import com.thedndpub.data.dto.spell.SpellNormalizationResult;
import com.thedndpub.data.dto.spell.SpellRange;
import com.thedndpub.data.dto.spell.SpellReprintReference;
import com.thedndpub.data.dto.spell.SpellScaling;
import com.thedndpub.data.dto.spell.SpellScalingValue;
import com.thedndpub.data.dto.spell.SpellSourceRelationships;
import com.thedndpub.data.dto.spell.SpellValidationSummary;
import com.thedndpub.data.dte.spell.ConsumeDte;
import com.thedndpub.data.dte.spell.ConsumeFlagDte;
import com.thedndpub.data.dte.spell.ConsumeNoteDte;
import com.thedndpub.data.dte.spell.MaterialDetailsDte;
import com.thedndpub.data.dte.spell.MaterialTextDte;
import com.thedndpub.data.dte.spell.ScalingLevelDiceDte;
import com.thedndpub.data.dte.spell.SpellCatalogReferenceDte;
import com.thedndpub.data.dte.spell.SpellDte;
import com.thedndpub.data.dte.spell.SpellSourceDte;
import com.thedndpub.data.services.SourceRegistry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** Resolves spell identities and reprint links without evaluating spell rules. */
public final class SpellDefinitionNormalizer {
    private final SourceRegistry sourceRegistry;

    public SpellDefinitionNormalizer(SourceRegistry sourceRegistry) {
        this.sourceRegistry = sourceRegistry;
    }

    public SpellNormalizationResult normalize(SpellSourceCorpus corpus) {
        List<SpellRecord> records = collectSpellRecords(corpus);
        Map<String, SpellRecord> recordsByIdentity = indexIdentities(records);
        Set<String> validatedSources = new HashSet<>();
        for (SpellRecord record : records) {
            validateSources(record.data(), record.location(), validatedSources);
        }

        List<SpellDefinition> definitions = new ArrayList<>(records.size());
        int resolvedReprintReferences = 0;
        for (SpellRecord record : records) {
            SpellDefinition definition = normalize(record, recordsByIdentity);
            definitions.add(definition);
            resolvedReprintReferences += definition.sourceRelationships().reprintedAs().size();
        }
        definitions.sort(Comparator.comparing(definition -> definition.id().value()));

        return new SpellNormalizationResult(definitions,
                new SpellValidationSummary(corpus.documents().size(), definitions.size(),
                        validatedSources.size(), resolvedReprintReferences));
    }

    private List<SpellRecord> collectSpellRecords(SpellSourceCorpus corpus) {
        List<SpellRecord> records = new ArrayList<>();
        for (SpellSourceDocument document : corpus.documents()) {
            List<SpellDte> spells = document.data().spells();
            for (int index = 0; index < spells.size(); index++) {
                SpellDte spell = spells.get(index);
                String location = document.resourceName() + "#/spell/" + index;
                requireIdentity(spell, location);
                records.add(new SpellRecord(spell, location,
                        SpellIdentityFactory.id(spell.name(), spell.source()),
                        SpellIdentityFactory.identity(spell.name(), spell.source())));
            }
        }
        return List.copyOf(records);
    }

    private Map<String, SpellRecord> indexIdentities(List<SpellRecord> records) {
        Map<String, SpellRecord> recordsByIdentity = new HashMap<>();
        Map<String, SpellRecord> recordsById = new HashMap<>();
        for (SpellRecord record : records) {
            String identityKey = SpellIdentityFactory.lookupKey(
                    record.data().name(), record.data().source());
            SpellRecord previousIdentity = recordsByIdentity.putIfAbsent(identityKey, record);
            if (previousIdentity != null) {
                throw new IllegalStateException("Duplicate spell identity " + identityKey + " at "
                        + previousIdentity.location() + " and " + record.location());
            }
            SpellRecord previousId = recordsById.putIfAbsent(record.id().value(), record);
            if (previousId != null) {
                throw new IllegalStateException("Spell ID collision " + record.id().value() + " at "
                        + previousId.location() + " and " + record.location());
            }
        }
        return Map.copyOf(recordsByIdentity);
    }

    private void validateSources(SpellDte spell, String location, Set<String> validatedSources) {
        requireRegisteredSource(spell.source(), location + ".source", validatedSources);
        validateSourceReferences(spell.additionalSources(), location + ".additionalSources", validatedSources);
        validateSourceReferences(spell.otherSources(), location + ".otherSources", validatedSources);
        for (int index = 0; index < safe(spell.referenceSources()).size(); index++) {
            requireRegisteredSource(spell.referenceSources().get(index),
                    location + ".referenceSources[" + index + "]", validatedSources);
        }
        for (int index = 0; index < safe(spell.reprintedAs()).size(); index++) {
            SpellUid target = parseSpellUid(spell.reprintedAs().get(index),
                    location + ".reprintedAs[" + index + "]");
            requireRegisteredSource(target.source(), location + ".reprintedAs[" + index + "]", validatedSources);
        }
    }

    private void validateSourceReferences(List<SpellSourceDte> references, String location,
                                         Set<String> validatedSources) {
        for (int index = 0; index < safe(references).size(); index++) {
            SpellSourceDte reference = references.get(index);
            if (reference == null) {
                throw new IllegalStateException("Null source reference at " + location + "[" + index + "]");
            }
            requireRegisteredSource(reference.source(), location + "[" + index + "]", validatedSources);
        }
    }

    private void requireRegisteredSource(String source, String location, Set<String> validatedSources) {
        if (source == null || source.isBlank()) {
            throw new IllegalStateException("Missing source identity at " + location);
        }
        if (sourceRegistry.find(source) == null) {
            throw new IllegalStateException("Unknown spell source identity '" + source + "' at " + location);
        }
        validatedSources.add(source.toLowerCase(Locale.ROOT));
    }

    private SpellDefinition normalize(SpellRecord record, Map<String, SpellRecord> recordsByIdentity) {
        SpellDte source = record.data();
        List<SpellCastingTime> castingTimes = safe(source.time()).stream()
                .map(value -> new SpellCastingTime(value.number(), value.unit(), value.condition(), value.note()))
                .toList();
        List<SpellDuration> durations = safe(source.duration()).stream()
                .map(value -> new SpellDuration(value.type(), value.concentration(),
                        value.duration() == null ? null : new SpellDurationAmount(
                                value.duration().type(), value.duration().amount(), value.duration().upTo()),
                        safe(value.ends())))
                .toList();
        List<SpellScaling> scaling = safe(source.scalingLevelDice()).stream()
                .map(this::scaling)
                .toList();

        return new SpellDefinition(
                record.id(),
                record.identity(),
                new SourceReference(source.source(), source.page()),
                source.level(),
                source.school(),
                safe(source.alias()),
                castingTimes,
                range(source),
                components(source),
                durations,
                source.meta() == null ? null : source.meta().ritual(),
                mechanics(source),
                scaling,
                safe(source.entries()),
                safe(source.entriesHigherLevel()),
                sourceRelationships(source, recordsByIdentity),
                importMetadata(source));
    }

    private SpellRange range(SpellDte source) {
        if (source.range() == null) {
            return null;
        }
        var distance = source.range().distance();
        return new SpellRange(source.range().type(), distance == null
                ? null
                : new SpellDistance(distance.type(), distance.amount()));
    }

    private SpellComponents components(SpellDte source) {
        if (source.components() == null) {
            return null;
        }
        return new SpellComponents(source.components().v(), source.components().s(), source.components().r(),
                material(source.components().m()));
    }

    private SpellMaterial material(com.thedndpub.data.dte.spell.MaterialComponentDte source) {
        return switch (source) {
            case null -> null;
            case MaterialTextDte text -> new SpellMaterialText(text.text());
            case MaterialDetailsDte details -> new SpellMaterialDetails(details.text(), details.cost(),
                    consume(details.consume()));
        };
    }

    private SpellConsume consume(ConsumeDte source) {
        return switch (source) {
            case null -> null;
            case ConsumeFlagDte flag -> new SpellConsumeFlag(flag.value());
            case ConsumeNoteDte note -> new SpellConsumeNote(note.value());
        };
    }

    private SpellMechanics mechanics(SpellDte source) {
        return new SpellMechanics(safe(source.abilityCheck()), safe(source.affectsCreatureType()),
                safe(source.areaTags()), safe(source.conditionImmune()), safe(source.conditionInflict()),
                safe(source.damageImmune()), safe(source.damageInflict()), safe(source.damageResist()),
                safe(source.damageVulnerable()), safe(source.miscTags()), safe(source.savingThrow()),
                safe(source.spellAttack()));
    }

    private SpellScaling scaling(ScalingLevelDiceDte source) {
        List<SpellScalingValue> values = source.scaling().stream()
                .map(value -> new SpellScalingValue(value.levelKey(), value.formula()))
                .toList();
        return new SpellScaling(source.label(), values);
    }

    private SpellSourceRelationships sourceRelationships(SpellDte source,
                                                         Map<String, SpellRecord> recordsByIdentity) {
        List<SourceReference> additionalSources = mapSourceReferences(source.additionalSources());
        List<SourceReference> otherSources = mapSourceReferences(source.otherSources());
        List<SourceIdentity> referenceSources = safe(source.referenceSources()).stream()
                .map(SourceIdentity::new)
                .toList();
        List<SpellReprintReference> reprints = new ArrayList<>();
        for (String uid : safe(source.reprintedAs())) {
            SpellUid target = parseSpellUid(uid, source.name() + "|" + source.source() + ".reprintedAs");
            SpellRecord resolved = recordsByIdentity.get(
                    SpellIdentityFactory.lookupKey(target.name(), target.source()));
            if (resolved == null) {
                throw new IllegalStateException("Unresolved spell reprint target '" + uid + "' from "
                        + source.name() + "|" + source.source());
            }
            reprints.add(new SpellReprintReference(resolved.id(), resolved.identity()));
        }
        return new SpellSourceRelationships(additionalSources, otherSources, referenceSources, reprints);
    }

    private List<SourceReference> mapSourceReferences(List<SpellSourceDte> references) {
        return safe(references).stream()
                .map(reference -> new SourceReference(reference.source(), reference.page()))
                .toList();
    }

    private SpellImportMetadata importMetadata(SpellDte source) {
        return new SpellImportMetadata(source.basicRules(), source.basicRules2024(), source.hasFluff(),
                source.hasFluffImages(), catalogReference(source.srd()), catalogReference(source.srd52()));
    }

    private SpellCatalogReference catalogReference(SpellCatalogReferenceDte source) {
        return source == null ? null : new SpellCatalogReference(source.listed(), source.alternateName());
    }

    private SpellUid parseSpellUid(String uid, String location) {
        if (uid == null) {
            throw new IllegalStateException("Null spell UID at " + location);
        }
        int separator = uid.lastIndexOf('|');
        if (separator <= 0 || separator == uid.length() - 1) {
            throw new IllegalStateException("Spell UID must include a non-empty name and source at "
                    + location + ": " + uid);
        }
        return new SpellUid(uid.substring(0, separator), uid.substring(separator + 1));
    }

    private void requireIdentity(SpellDte spell, String location) {
        if (spell == null || spell.name() == null || spell.name().isBlank()
                || spell.source() == null || spell.source().isBlank()) {
            throw new IllegalStateException("Spell at " + location + " is missing its name or source identity");
        }
    }

    private static <T> List<T> safe(List<T> values) {
        return values == null ? List.of() : values;
    }

    private record SpellRecord(SpellDte data, String location, com.thedndpub.data.dto.spell.SpellId id,
                               SpellIdentity identity) {
    }

    private record SpellUid(String name, String source) {
    }
}
