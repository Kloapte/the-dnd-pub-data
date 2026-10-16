package com.thedndpub.data.importer.classes;

import com.thedndpub.data.dto.classes.ClassDefinition;
import com.thedndpub.data.dto.classes.ClassDefinitionKind;
import com.thedndpub.data.dto.classes.ClassEquipmentOption;
import com.thedndpub.data.dto.classes.ClassExternalFeatureNode;
import com.thedndpub.data.dto.classes.ClassExternalKind;
import com.thedndpub.data.dto.classes.ClassExternalReference;
import com.thedndpub.data.dto.classes.ClassFeatureChoice;
import com.thedndpub.data.dto.classes.ClassFeatureConsumption;
import com.thedndpub.data.dto.classes.ClassFeatureDefinition;
import com.thedndpub.data.dto.classes.ClassFeatureId;
import com.thedndpub.data.dto.classes.ClassFeatureNode;
import com.thedndpub.data.dto.classes.ClassFeatureSection;
import com.thedndpub.data.dto.classes.ClassFeatureStatblock;
import com.thedndpub.data.dto.classes.ClassFeatureUnlock;
import com.thedndpub.data.dto.classes.ClassFileMetadata;
import com.thedndpub.data.dto.classes.ClassHitDice;
import com.thedndpub.data.dto.classes.ClassId;
import com.thedndpub.data.dto.classes.ClassImportProvenance;
import com.thedndpub.data.dto.classes.ClassNormalizationResult;
import com.thedndpub.data.dto.classes.ClassProficiencyOptions;
import com.thedndpub.data.dto.classes.ClassPublicationFlags;
import com.thedndpub.data.dto.classes.ClassReferenceStatus;
import com.thedndpub.data.dto.classes.ClassSourceRelationships;
import com.thedndpub.data.dto.classes.ClassSpellReference;
import com.thedndpub.data.dto.classes.ClassSpellcasting;
import com.thedndpub.data.dto.classes.ClassValidationSummary;
import com.thedndpub.data.dto.classes.SubclassDefinition;
import com.thedndpub.data.dto.classes.SubclassFeatureDefinition;
import com.thedndpub.data.dto.classes.SubclassFeatureId;
import com.thedndpub.data.dto.classes.SubclassId;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.classes.ClassDte;
import com.thedndpub.data.dte.classes.ClassFeatureConsumptionDte;
import com.thedndpub.data.dte.classes.ClassFeatureDte;
import com.thedndpub.data.dte.classes.ClassRootDte;
import com.thedndpub.data.dte.classes.ClassSourceReferenceDte;
import com.thedndpub.data.dte.classes.ClassSpellcastingSourceDte;
import com.thedndpub.data.dte.classes.SubclassDte;
import com.thedndpub.data.dte.classes.SubclassFeatureDte;
import com.thedndpub.data.services.SourceRegistry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static com.thedndpub.data.importer.classes.ClassReferenceResolver.safe;

/** Stateless Stage B orchestration; callers explicitly load and normalize an immutable import result. */
public final class ClassDefinitionNormalizer {
    private final SourceRegistry registry;
    private final ClassExternalReferenceResolver external;

    public ClassDefinitionNormalizer(SourceRegistry registry, ClassExternalReferenceResolver external) {
        this.registry = registry;
        this.external = external;
    }

    public ClassNormalizationResult normalize(ClassSourceCorpus corpus) {
        var sources = new ClassSourceValidator(registry).validate(corpus);
        var resolved = new ClassCopyResolver().resolve(corpus);
        var references = new ClassReferenceResolver(resolved);
        var mechanics = new ClassMechanicsNormalizer(external);
        var spells = new ClassSpellcastingNormalizer();
        var content = new ClassFeatureContentNormalizer(references, external);
        List<ClassDefinition> classes = new ArrayList<>();
        List<SubclassDefinition> subclasses = new ArrayList<>();
        List<ClassFeatureDefinition> classFeatures = new ArrayList<>();
        List<SubclassFeatureDefinition> subclassFeatures = new ArrayList<>();

        for (ResolvedClassRecord record : resolved) {
            ClassRootDte root = record.data();
            var identity = ClassIdentities.identity(root);
            String id = ClassIdentities.id(identity);
            var provenance = new ClassImportProvenance(record.authored(), root, record.copyBases(),
                    record.inheritedFields(), record.suppressedMetadata());
            SourceReference source = new SourceReference(root.source(), root.page());
            switch (root) {
                case ClassDte value -> {
                    var tables = mechanics.tables(value.classTableGroups());
                    ClassId classId = new ClassId(id);
                    ClassHitDice hitDice = value.hd() == null ? null
                            : new ClassHitDice(value.hd().number(), value.hd().faces());
                    classes.add(new ClassDefinition(classId, identity, value.name(), source, value.edition(),
                            value.isSidekick(), hitDice, value.proficiency(),
                            safe(value.primaryAbility()).stream().map(ClassMechanicsNormalizer::abilities).toList(),
                            mechanics.starting(value.startingProficiencies(), value.startingEquipment()),
                            mechanics.multiclass(value.multiclassing()), classUnlocks(value, references),
                            references.subclasses(classId), tables,
                            mechanics.choices(value.optionalfeatureProgression(), value.featProgression()),
                            spells.normalize(value, tables), relationships(root, references), provenance));
                }
                case SubclassDte value -> {
                    var tables = mechanics.tables(value.subclassTableGroups());
                    subclasses.add(new SubclassDefinition(new SubclassId(id), identity, value.name(), value.shortName(), source,
                            references.parentClass(value.className(), value.classSource()), value.edition(),
                            subclassUnlocks(value, references), tables,
                            mechanics.choices(value.optionalfeatureProgression(), value.featProgression()),
                            spells.normalize(value, tables), relationships(root, references), provenance));
                }
                case ClassFeatureDte value -> classFeatures.add(new ClassFeatureDefinition(new ClassFeatureId(id),
                        identity, value.name(), source, references.parentClass(value.className(), value.classSource()),
                        value.level(), value.isClassFeatureVariant(), consumes(value.consumes()), content.normalize(value.entries()),
                        relationships(root, references), provenance));
                case SubclassFeatureDte value -> subclassFeatures.add(new SubclassFeatureDefinition(new SubclassFeatureId(id),
                        identity, value.name(), source, references.parentClass(value.className(), value.classSource()),
                        references.parentSubclass(value.className(), value.classSource(),
                                value.subclassShortName(), value.subclassSource()),
                        value.level(), value.isClassFeatureVariant(), consumes(value.consumes()), content.normalize(value.entries()),
                        relationships(root, references), provenance));
            }
        }
        classes.sort(Comparator.comparing(value -> value.id().value()));
        subclasses.sort(Comparator.comparing(value -> value.id().value()));
        classFeatures.sort(Comparator.comparing(value -> value.id().value()));
        subclassFeatures.sort(Comparator.comparing(value -> value.id().value()));
        int directSpells = 0;
        int choiceSpells = 0;
        for (var root : corpus.records()) {
            if (root.data() instanceof ClassSpellcastingSourceDte spellSource) {
                for (var group : spells.groups(spellSource.additionalSpells())) {
                    for (var grant : group.grants()) {
                        for (var selection : grant.selections()) {
                            directSpells += selection.spell() == null ? 0 : 1;
                            choiceSpells += selection.from().size();
                        }
                    }
                }
            }
        }
        int subclassCopies = (int) resolved.stream().filter(record -> record.data() instanceof SubclassDte
                && !record.copyBases().isEmpty()).count();
        int featureCopies = (int) resolved.stream().filter(record -> record.data() instanceof SubclassFeatureDte
                && !record.copyBases().isEmpty()).count();
        int primarySources = (int) corpus.records().stream()
                .map(record -> record.data().source().toLowerCase(Locale.ROOT)).distinct().count();
        var validation = new ClassValidationSummary(references.authoredReferenceCount(), references.edges().size(),
                directSpells, choiceSpells, subclassCopies, featureCopies, references.orphanCount(), primarySources, sources.size());
        List<ClassExternalReference> externalReferences = collectExternalReferences(classes, subclasses, classFeatures, subclassFeatures);
        return new ClassNormalizationResult(classes, subclasses, classFeatures, subclassFeatures,
                references.edges(), externalReferences, validation, metadata(corpus));
    }

    private static List<ClassFileMetadata> metadata(ClassSourceCorpus corpus) {
        return corpus.documents().stream()
                .filter(document -> document.data().metadata() != null)
                .sorted(Comparator.comparing(ClassSourceDocument::resource))
                .map(document -> new ClassFileMetadata(document.resource(),
                        safe(document.data().metadata().internalCopies()).stream().map(kind -> switch (kind) {
                            case "subclass" -> ClassDefinitionKind.SUBCLASS;
                            case "subclassFeature" -> ClassDefinitionKind.SUBCLASS_FEATURE;
                            default -> throw new IllegalStateException("Unsupported class internal-copy kind: " + kind);
                        }).toList()))
                .toList();
    }

    private static List<ClassFeatureUnlock> classUnlocks(ClassDte source, ClassReferenceResolver references) {
        List<ClassFeatureUnlock> unlocks = new ArrayList<>();
        for (var unlock : safe(source.classFeatures())) {
            var reference = references.feature(ClassDefinitionKind.CLASS_FEATURE, unlock.classFeature());
            unlocks.add(new ClassFeatureUnlock(reference, reference.identity().level(), unlocks.size(),
                    unlock.gainSubclassFeature(), unlock.gainSubclassFeatureHasContent(), unlock.tableDisplayName()));
        }
        return List.copyOf(unlocks);
    }

    private static List<ClassFeatureUnlock> subclassUnlocks(SubclassDte source, ClassReferenceResolver references) {
        List<ClassFeatureUnlock> unlocks = new ArrayList<>();
        for (String uid : safe(source.subclassFeatures())) {
            var reference = references.feature(ClassDefinitionKind.SUBCLASS_FEATURE, uid);
            unlocks.add(new ClassFeatureUnlock(reference, reference.identity().level(), unlocks.size(), null, null, null));
        }
        return List.copyOf(unlocks);
    }

    private static ClassFeatureConsumption consumes(ClassFeatureConsumptionDte source) {
        return source == null ? null : new ClassFeatureConsumption(source.name(), source.amount(), source.amountMin(), source.amountMax());
    }

    private static ClassSourceRelationships relationships(ClassRootDte root, ClassReferenceResolver references) {
        List<ClassSourceReferenceDte> secondary;
        List<String> reprints = List.of();
        ClassPublicationFlags flags;
        switch (root) {
            case ClassDte value -> {
                secondary = value.otherSources();
                reprints = safe(value.reprintedAs());
                flags = new ClassPublicationFlags(value.srd(), value.srd52(), value.basicRules(), value.basicRules2024());
            }
            case SubclassDte value -> {
                secondary = value.otherSources();
                reprints = safe(value.reprintedAs());
                flags = new ClassPublicationFlags(value.srd(), value.srd52(), value.basicRules(), value.basicRules2024());
            }
            case ClassFeatureDte value -> {
                secondary = value.otherSources();
                flags = new ClassPublicationFlags(value.srd(), value.srd52(), value.basicRules(), value.basicRules2024());
            }
            case SubclassFeatureDte value -> {
                secondary = value.otherSources();
                flags = new ClassPublicationFlags(value.srd(), value.srd52(), value.basicRules(), value.basicRules2024());
            }
        }
        ClassDefinitionKind kind = ClassIdentities.identity(root).kind();
        return new ClassSourceRelationships(safe(secondary).stream()
                .map(value -> new SourceReference(value.source(), value.page())).toList(),
                reprints.stream().map(uid -> references.reprint(kind, uid)).toList(), flags);
    }

    private static List<ClassExternalReference> collectExternalReferences(
            List<ClassDefinition> classes,
            List<SubclassDefinition> subclasses,
            List<ClassFeatureDefinition> classFeatures,
            List<SubclassFeatureDefinition> subclassFeatures) {
        List<ClassExternalReference> result = new ArrayList<>();
        for (var value : classes) {
            if (value.startingOptions() != null) {
                for (var group : value.startingOptions().equipment()) {
                    for (var branch : group.branches()) {
                        branch.entries().stream().map(ClassEquipmentOption::reference)
                                .filter(java.util.Objects::nonNull).forEach(result::add);
                    }
                }
                skills(result, value.startingOptions().proficiencies());
            }
            if (value.multiclassing() != null) {
                skills(result, value.multiclassing().proficienciesGained());
            }
            spellReferences(result, value.spellcasting());
            value.choices().forEach(choice -> choice.required().forEach(required -> result.add(required.reference())));
        }
        for (var value : subclasses) {
            spellReferences(result, value.spellcasting());
            value.choices().forEach(choice -> choice.required().forEach(required -> result.add(required.reference())));
        }
        classFeatures.forEach(feature -> contentReferences(result, feature.content()));
        subclassFeatures.forEach(feature -> contentReferences(result, feature.content()));
        return List.copyOf(result);
    }

    private static void skills(List<ClassExternalReference> target, ClassProficiencyOptions options) {
        if (options != null) {
            options.skills().forEach(choice -> target.addAll(choice.from()));
        }
    }

    private static void spellReferences(List<ClassExternalReference> result, ClassSpellcasting spells) {
        if (spells == null) {
            return;
        }
        for (var group : spells.additionalSpells()) {
            for (var grant : group.grants()) {
                for (var selection : grant.selections()) {
                    List<ClassSpellReference> references = new ArrayList<>(selection.from());
                    if (selection.spell() != null) {
                        references.add(selection.spell());
                    }
                    references.forEach(reference -> result.add(new ClassExternalReference(ClassExternalKind.SPELL,
                            reference.name(), reference.source(), null, ClassReferenceStatus.DEFERRED_SPELL)));
                }
            }
        }
    }

    private static void contentReferences(List<ClassExternalReference> result, List<ClassFeatureNode> content) {
        for (ClassFeatureNode node : content) {
            if (node instanceof ClassExternalFeatureNode external) {
                result.add(external.reference());
            } else if (node instanceof ClassFeatureStatblock statblock) {
                result.add(statblock.reference());
            } else if (node instanceof ClassFeatureSection section) {
                contentReferences(result, section.entries());
                contentReferences(result, section.items());
            } else if (node instanceof ClassFeatureChoice choice) {
                contentReferences(result, choice.options());
            }
        }
    }
}
