package com.thedndpub.data.mechanics.model;

import com.thedndpub.data.mechanics.review.MechanicsReviewStatus;
import com.thedndpub.data.mechanics.syntax.SyntaxSourceOwner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Joins typed references and checks integrity. Source reading and mechanical interpretation belong to adapters. */
final class MechanicsDocumentValidator {
    private MechanicsDocumentValidator() {
    }

    static List<MechanicRecord> prepare(
            MechanicsOwnerReference owner,
            List<SourceArtifactRevision> sources,
            MechanicsProcessing processing,
            List<MechanicRecord> records,
            List<MechanicsObservationReference> observations,
            List<UnsupportedMechanicsConstruct> unsupported,
            List<MechanicsEvidence> evidence,
            MechanicsCoverage coverage) {
        Map<String, SourceArtifactRevision> sourceIndex = new LinkedHashMap<>();
        for (SourceArtifactRevision source : sources) {
            ModelChecks.require(sourceIndex.putIfAbsent(source.resourcePath(), source) == null,
                    "Duplicate source resource revision");
        }
        Map<String, MechanicsEvidence> evidenceIndex = new LinkedHashMap<>();
        for (MechanicsEvidence item : evidence) {
            ModelChecks.require(evidenceIndex.putIfAbsent(item.id(), item) == null, "Duplicate evidence ID");
            if (item instanceof TextMechanicsEvidence text) {
                source(sourceIndex, text.syntax().location().owner());
                originalOwner(owner, text.source().identity().code(), text.origin(), text.authoredOwner());
            } else if (item instanceof StructuredFieldMechanicsEvidence field) {
                source(sourceIndex, field.owner());
                originalOwner(owner, field.source().identity().code(), field.origin(), field.authoredOwner());
            } else if (item instanceof TransformedMechanicsEvidence transformed) {
                ModelChecks.require(owner.equals(transformed.effectiveOwner()), "Transformed evidence owner mismatch");
            } else if (item instanceof UnavailableOriginMechanicsEvidence unavailable) {
                ModelChecks.require(owner.equals(unavailable.effectiveOwner()), "Unavailable-origin evidence owner mismatch");
            }
        }
        for (MechanicsEvidence item : evidence) {
            validateEvidenceGraph(item.id(), evidenceIndex, new HashSet<>(), new HashSet<>());
        }
        Map<String, MechanicsObservationReference> observationIndex = new LinkedHashMap<>();
        for (MechanicsObservationReference observation : observations) {
            ModelChecks.require(observationIndex.putIfAbsent(observation.id(), observation) == null, "Duplicate observation ID");
            if (observation instanceof FormulaObservationReference formula) {
                ModelChecks.require(owner.domain() == CatalogDomain.SPELL && owner.id().equals(formula.spellId().value()),
                        "Formula observation belongs to another owner");
                source(sourceIndex, formula.evidence().location().owner());
                ModelChecks.require(formula.source().equals(sourceIndex.get(formula.source().resourcePath())),
                        "Formula artifact revision mismatch");
            } else if (observation instanceof StructuredDeclarationReference declaration) {
                ModelChecks.require(declaration.source().equals(sourceIndex.get(declaration.source().resourcePath())),
                        "Structured declaration artifact mismatch");
            } else if (observation instanceof OtherSyntaxObservationReference syntax) {
                source(sourceIndex, syntax.evidence().location().owner());
            }
        }
        Map<MechanicId, MechanicRecord> recordIndex = new LinkedHashMap<>();
        for (MechanicRecord record : records) {
            ModelChecks.require(record.id().ownerId().equals(owner.id()), "Mechanic belongs to another catalog owner");
            ModelChecks.require(recordIndex.putIfAbsent(record.id(), record) == null, "Duplicate current mechanic ID");
        }
        List<MechanicRecord> prepared = new ArrayList<>();
        for (MechanicRecord record : records) {
            List<MechanicsFinding> findings = new ArrayList<>();
            for (SourceArtifactRevision source : record.derivation().sources()) {
                ModelChecks.require(source.equals(sourceIndex.get(source.resourcePath())), "Claim source revision mismatch");
            }
            for (ObservationRevisionReference reference : record.derivation().inputObservations()) {
                MechanicsObservationReference observation = observationIndex.get(reference.id());
                ModelChecks.require(observation != null
                        && reference.contentSha256().equals(MechanicsHashes.observationHash(observation)),
                        "Missing or changed input observation");
            }
            for (MechanicEvidenceAssociation association : record.evidenceAssociations()) {
                MechanicsEvidence item = evidenceIndex.get(association.evidenceId());
                ModelChecks.require(item != null && association.evidenceSha256().equals(MechanicsHashes.evidenceHash(item)),
                        "Missing or changed claim evidence");
                if (unavailable(item, evidenceIndex, new HashSet<>())) {
                    MechanicsQuality.add(findings, MechanicsFindingCode.UNAVAILABLE_ORIGIN, association.claimPath());
                }
            }
            for (MechanicsFinding finding : record.review().findings()) {
                ModelChecks.require(evidenceIndex.keySet().containsAll(finding.evidenceIds()), "Unknown finding evidence");
            }
            for (MechanicRevisionReference reference : MechanicsBindings.references(record)) {
                ModelChecks.require(reference.id().ownerId().equals(owner.id()), "Cross-owner context binding is unsupported");
                MechanicRecord target = recordIndex.get(reference.id());
                if (target == null) {
                    MechanicsQuality.add(findings, MechanicsFindingCode.ESSENTIAL_REFERENCE_NOT_RESOLVED, "/context");
                } else if (!target.revision().reference(target.id()).equals(reference)) {
                    MechanicsQuality.add(findings, MechanicsFindingCode.STALE_EVIDENCE, "/context");
                }
            }
            validateBindingTypes(record, recordIndex);
            validateDurationSources(record, sourceIndex, owner);
            prepared.add(record.withReviewFindings(findings));
        }
        // Invalidated/rejected dependencies cannot leave a dependent claim silently verified.
        for (int pass = 0; pass < prepared.size(); pass++) {
            Map<MechanicId, MechanicRecord> current = new HashMap<>();
            prepared.forEach(record -> current.put(record.id(), record));
            boolean changed = false;
            for (int index = 0; index < prepared.size(); index++) {
                MechanicRecord record = prepared.get(index);
                List<MechanicsFinding> findings = new ArrayList<>();
                for (MechanicRevisionReference reference : MechanicsBindings.references(record)) {
                    MechanicRecord target = current.get(reference.id());
                    if (target != null && (target.review().effectiveStatus() == MechanicsReviewStatus.REJECTED
                            || target.review().effectiveStatus() == MechanicsReviewStatus.NEEDS_MECHANICS_REVIEW)) {
                        MechanicsQuality.add(findings, MechanicsFindingCode.DEPENDENCY_REQUIRES_REVIEW, "/context");
                    }
                }
                MechanicRecord updated = record.withReviewFindings(findings);
                if (!record.equals(updated)) {
                    prepared.set(index, updated);
                    changed = true;
                }
            }
            if (!changed) {
                break;
            }
        }
        Set<String> unsupportedIds = new HashSet<>();
        for (UnsupportedMechanicsConstruct construct : unsupported) {
            ModelChecks.require(unsupportedIds.add(construct.id()), "Duplicate unsupported construct ID");
            ModelChecks.require(evidenceIndex.keySet().containsAll(construct.evidenceIds()), "Unknown unsupported evidence");
        }
        ModelChecks.require(coverage.unsupportedCount() == unsupported.size(), "Unsupported count mismatch");
        if (processing.state() == ProcessingState.NOT_ATTEMPTED || processing.state() == ProcessingState.FAILED) {
            ModelChecks.require(records.isEmpty() && coverage.status() == CoverageStatus.NOT_ASSESSED,
                    "Unattempted/failed processing cannot claim successful interpretations");
        } else {
            ModelChecks.require(coverage.status() != CoverageStatus.NOT_ASSESSED, "Processed scope needs a coverage assessment");
        }
        if (coverage.status() == CoverageStatus.NONE_SUPPORTED) {
            ModelChecks.require(records.isEmpty(), "NONE_SUPPORTED cannot contain interpretations");
        }
        if (coverage.status() == CoverageStatus.COMPLETE_WITHIN_SCOPE) {
            ModelChecks.require(prepared.stream().allMatch(record ->
                    record.assessment().completeness() == MechanicsCompleteness.COMPLETE_WITHIN_CLAIM
                            && record.review().findings().stream()
                                .noneMatch(finding -> finding.severity() == FindingSeverity.REQUIRES_REVIEW)),
                    "Complete scope contains partial claims");
        }
        return List.copyOf(prepared);
    }

    private static void validateBindingTypes(MechanicRecord record, Map<MechanicId, MechanicRecord> records) {
        if (record.payload() instanceof DamageMechanic damage
                && damage.amount() instanceof FractionOfAmount fraction) {
            MechanicRecord base = records.get(fraction.baseAmount().id());
            ModelChecks.require(base != null && base.payload() instanceof DamageMechanic,
                    "Fractional damage must reference an existing damage claim");
            ModelChecks.require(base.revision().reference(base.id()).equals(fraction.baseAmount()),
                    "Fractional damage references a stale base damage revision");
            DamageMechanic baseDamage = (DamageMechanic) base.payload();
            ModelChecks.require(!(baseDamage.amount() instanceof FractionOfAmount),
                    "Fractional damage cannot reference another fraction");
            ModelChecks.require(damage.damageType().equals(baseDamage.damageType()),
                    "Fractional damage must preserve its base damage type");
        }
        for (MechanicContextBinding binding : record.context().bindings()) {
            if (binding instanceof ActivatedBy activation) {
                MechanicRecord target = current(records, activation.activation());
                ModelChecks.require(target == null || target.payload() instanceof ReactionActivationMechanic,
                        "Activation binding must reference an activation mechanic");
            } else if (binding instanceof AppliesOnSaveOutcome outcome) {
                ModelChecks.require(record.payload() instanceof DamageMechanic,
                        "Save damage outcome must be attached to a damage mechanic");
                MechanicRecord target = current(records, outcome.savingThrow());
                ModelChecks.require(target == null || target.payload() instanceof SavingThrowRequirementMechanic,
                        "Save outcome binding must reference a save requirement");
            } else if (binding instanceof AppliesOnAttackRollOutcome outcome) {
                ModelChecks.require(record.payload() instanceof DamageMechanic
                                || record.payload() instanceof StatisticModifierMechanic
                                || record.payload() instanceof HitPointRecoveryPreventionMechanic
                                || record.payload() instanceof ReactionPreventionMechanic
                                || record.payload() instanceof AttackRollModifierMechanic,
                        "Attack-roll outcome must be attached to a supported effect mechanic");
                MechanicRecord target = records.get(outcome.attackRoll().id());
                ModelChecks.require(target == null || target.payload() instanceof AttackRollMechanic,
                        "Attack-roll outcome must reference an attack-roll mechanic");
                if (target != null) {
                    ModelChecks.require(target.revision().reference(target.id()).equals(outcome.attackRoll()),
                            "Attack-roll outcome references a stale attack-roll revision");
                }
            } else if (binding instanceof AppliesOnEachAttackRollOutcome outcome) {
                ModelChecks.require(record.payload() instanceof DamageMechanic,
                        "Per-instance attack-roll outcome must be attached to damage");
                MechanicRecord target = records.get(outcome.attackRoll().id());
                ModelChecks.require(target != null && target.payload() instanceof AttackRollMechanic,
                        "Per-instance outcome must reference an existing attack-roll mechanic");
                ModelChecks.require(target.revision().reference(target.id()).equals(outcome.attackRoll()),
                        "Per-instance outcome references a stale attack-roll revision");
                AttackRollMechanic attack = (AttackRollMechanic) target.payload();
                ModelChecks.require(attack.attacksIndependent(),
                        "Per-instance outcome must reference independent attack rolls");
                ModelChecks.require(record.context().subject().equals(target.context().subject()),
                        "Per-instance damage must share the attack target subject");
            } else if (binding instanceof ConditionOnSaveOutcome outcome) {
                conditionPayload(record);
                validateSavingThrowTarget(records, outcome.savingThrow());
            } else if (binding instanceof ConditionEndsOnSaveSuccess ending) {
                conditionPayload(record);
                validateSavingThrowTarget(records, ending.savingThrow());
            } else if (binding instanceof ModifierOnSaveOutcome outcome) {
                ModelChecks.require(record.payload() instanceof StatisticModifierMechanic,
                        "Modifier save dependency must be attached to a statistic modifier");
                validateSavingThrowTarget(records, outcome.savingThrow());
            } else if (binding instanceof AppliesWhen) {
                ModelChecks.require(record.payload() instanceof StatisticModifierMechanic
                                || record.payload() instanceof ArmorClassModifierMechanic
                                || record.payload() instanceof DamageResistanceMechanic
                                || record.payload() instanceof AttackRollModifierMechanic,
                        "Applicability conditions must be attached to an effect claim");
            } else if (binding instanceof ConditionEndsWithSpell) {
                conditionPayload(record);
            } else if (binding instanceof RequiresConcentration) {
                ModelChecks.require(record.payload() instanceof ConditionChangeMechanic
                                || record.payload() instanceof ConditionChoiceMechanic
                                || record.payload() instanceof ArmorClassModifierMechanic
                                || record.payload() instanceof StatisticModifierMechanic
                                || record.payload() instanceof DamageResistanceMechanic
                                || record.payload() instanceof SpellDamagePreventionMechanic,
                        "Concentration must be attached to an effect claim");
            } else if (binding instanceof AppliesForDuration duration
                    && duration.duration() instanceof ReferencedDuration reference) {
                durationTarget(records, reference);
            }
        }
        validateInstanceTargetAllocation(record);
        if (record.payload() instanceof ConditionChangeMechanic condition && condition.choiceGroup() != null) {
            MechanicRecord target = current(records, condition.choiceGroup());
            ModelChecks.require(target == null || target.payload() instanceof ConditionChoiceMechanic,
                    "Condition choice reference must target a condition choice mechanic");
            if (target != null) {
                ConditionChoiceMechanic choice = (ConditionChoiceMechanic) target.payload();
                ModelChecks.require(choice.operation() == condition.operation()
                                && choice.alternatives().contains(condition.condition()),
                        "Condition claim does not match its choice group");
            }
        }
        validateAttackRollModifier(record, records);
        if (record.payload() instanceof EffectDurationMechanic duration
                && duration.specification() instanceof ReferencedDuration reference) {
            durationTarget(records, reference);
        }
    }

    private static void validateInstanceTargetAllocation(MechanicRecord record) {
        if (record.payload() instanceof DamageInstanceSeriesMechanic series) {
            InstanceTargetAllocation allocation = series.targetAllocation();
            ModelChecks.require(record.context().subject() instanceof SpellTargetSubject,
                    "Damage instance series requires a typed Spell target subject");
            validateTargetCategories((SpellTargetSubject) record.context().subject(),
                    allocation.eligibleTargetCategories());
            ModelChecks.require(record.context().bindings().isEmpty(),
                    "Automatic-hit damage series cannot depend on an attack-roll outcome");
            return;
        }
        if (!(record.payload() instanceof AttackRollMechanic attack) || attack.targetAllocation() == null) {
            return;
        }
        InstanceTargetAllocation allocation = attack.targetAllocation();
        if (allocation.targetCategoryEligibilityStatus() == TargetCategoryEligibilityStatus.UNSPECIFIED_IN_SOURCE) {
            ModelChecks.require(record.context().subject() instanceof UnknownSubject unknown
                            && unknown.reason() == UnknownValueReason.NOT_AUTHORED,
                    "Source-unspecified target categories require a not-authored subject value");
            return;
        }
        if (allocation.targetCategoryEligibilityStatus() == TargetCategoryEligibilityStatus.NOT_ASSESSED) {
            ModelChecks.require(record.context().subject() instanceof UnknownSubject unknown
                            && unknown.reason() == UnknownValueReason.NOT_INTERPRETED,
                    "Unassessed target categories require a not-interpreted subject value");
            return;
        }
        ModelChecks.require(record.context().subject() instanceof SpellTargetSubject,
                "Per-instance target allocation requires a typed Spell target subject");
        SpellTargetSubject subject = (SpellTargetSubject) record.context().subject();
        validateTargetCategories(subject, allocation.eligibleTargetCategories());
        if (attack.selectedTargetCount() != null) {
            ModelChecks.require(subject.scope() == TargetScope.MULTIPLE_TARGET_CREATURES
                            && subject.maximumTargets() != null
                            && subject.maximumTargets() == attack.selectedTargetCount().maximumSelectedTargets(),
                    "Selected-target attack count must match the target subject maximum");
        }
    }

    private static void validateTargetCategories(
            SpellTargetSubject subject,
            List<TargetCategory> allocationCategories) {
        List<TargetCategory> subjectCategories = switch (subject.scope()) {
            case SINGLE_TARGET_CREATURE -> List.of(TargetCategory.CREATURE);
            case MULTIPLE_TARGET_CREATURES -> List.of(TargetCategory.CREATURE);
            case SINGLE_TARGET -> subject.eligibleTargetCategories();
            default -> null;
        };
        ModelChecks.require(subjectCategories != null
                        && subjectCategories.equals(allocationCategories),
                "Per-instance target allocation must match the Spell target subject categories");
    }

    private static void validateAttackRollModifier(
            MechanicRecord record, Map<MechanicId, MechanicRecord> records) {
        if (!(record.payload() instanceof AttackRollModifierMechanic modifier)) {
            return;
        }
        if (modifier.affectedRolls() instanceof ReferencedAttackRollSelector referenced) {
            MechanicRecord target = records.get(referenced.attackRoll().id());
            ModelChecks.require(target != null && target.payload() instanceof AttackRollMechanic,
                    "A referenced attack-roll modifier must identify an existing attack-roll claim");
            ModelChecks.require(target.revision().reference(target.id()).equals(referenced.attackRoll()),
                    "An attack-roll modifier references a stale attack-roll revision");
        }
        List<ApplicabilityCondition> conditions = record.context().bindings().stream()
                .filter(AppliesWhen.class::isInstance)
                .map(AppliesWhen.class::cast)
                .map(AppliesWhen::condition)
                .toList();
        ModelChecks.require(!(modifier.affectedRolls() instanceof ReferencedAttackRollSelector
                        && conditions.contains(ApplicabilityCondition.TARGET_NOT_WEARING_ARMOR)
                        && conditions.contains(ApplicabilityCondition.ATTACK_ROLL_TARGET_WEARING_ARMOR_MADE_OF_METAL)),
                "A target cannot both wear metal armor and not wear armor");
    }

    private static void conditionPayload(MechanicRecord record) {
        if (record.payload() instanceof ConditionChangeMechanic condition) {
            ModelChecks.require(condition.operation() == ConditionOperation.APPLY,
                    "Condition relationships require an application claim");
            return;
        }
        ModelChecks.require(record.payload() instanceof ConditionChoiceMechanic choice
                        && choice.operation() == ConditionOperation.APPLY,
                "Condition relationship must target an application claim or choice group");
    }

    private static void validateSavingThrowTarget(
            Map<MechanicId, MechanicRecord> records, MechanicRevisionReference reference) {
        MechanicRecord target = current(records, reference);
        ModelChecks.require(target == null || target.payload() instanceof SavingThrowRequirementMechanic,
                "Condition save relationship must reference a save requirement");
    }

    private static void durationTarget(Map<MechanicId, MechanicRecord> records, ReferencedDuration reference) {
        MechanicRecord target = current(records, reference.mechanic());
        ModelChecks.require(target == null || target.payload() instanceof EffectDurationMechanic,
                "Duration binding must reference a duration mechanic");
    }

    private static MechanicRecord current(Map<MechanicId, MechanicRecord> records, MechanicRevisionReference reference) {
        MechanicRecord target = records.get(reference.id());
        return target != null && target.revision().reference(target.id()).equals(reference) ? target : null;
    }

    private static void validateDurationSources(
            MechanicRecord record, Map<String, SourceArtifactRevision> sources, MechanicsOwnerReference owner) {
        List<DurationSpecification> durations = new ArrayList<>();
        if (record.payload() instanceof EffectDurationMechanic duration) {
            durations.add(duration.specification());
        }
        for (MechanicContextBinding binding : record.context().bindings()) {
            if (binding instanceof AppliesForDuration duration) {
                durations.add(duration.duration());
            }
        }
        for (DurationSpecification duration : durations) {
            if (duration instanceof DeclaredSpellDurationReference declared) {
                ModelChecks.require(owner.id().equals(declared.spellId().value())
                        && declared.source().equals(sources.get(declared.source().resourcePath())),
                        "Declared duration source/owner mismatch");
            }
        }
        if (record.payload() instanceof DamageMechanic damage
                && damage.timing() == DamageTiming.END_OF_TARGET_NEXT_TURN) {
            ModelChecks.require(record.context().bindings().stream().anyMatch(binding ->
                            binding instanceof AppliesOnAttackRollOutcome outcome
                                    && outcome.outcome() == AttackRollOutcome.HIT),
                    "End-of-target-next-turn damage requires a HIT outcome trigger");
            ModelChecks.require(record.context().bindings().stream().noneMatch(binding ->
                            binding instanceof AppliesOnEachAttackRollOutcome),
                    "End-of-target-next-turn damage currently requires a single attack outcome");
        }
    }

    private static void validateEvidenceGraph(
            String id, Map<String, MechanicsEvidence> index, Set<String> visiting, Set<String> visited) {
        if (visited.contains(id)) {
            return;
        }
        ModelChecks.require(visiting.add(id), "Cyclic transformed evidence lineage");
        if (index.get(id) instanceof TransformedMechanicsEvidence transformed) {
            for (EvidenceRevisionReference input : transformed.inputs()) {
                MechanicsEvidence original = index.get(input.id());
                ModelChecks.require(original != null
                        && input.contentSha256().equals(MechanicsHashes.evidenceHash(original)),
                        "Missing or changed transformation input");
                validateEvidenceGraph(input.id(), index, visiting, visited);
            }
        }
        visiting.remove(id);
        visited.add(id);
    }

    private static boolean unavailable(MechanicsEvidence evidence, Map<String, MechanicsEvidence> index, Set<String> visited) {
        if (!visited.add(evidence.id())) {
            return false;
        }
        if (evidence instanceof UnavailableOriginMechanicsEvidence) {
            return true;
        }
        if (evidence instanceof TransformedMechanicsEvidence transformed) {
            return transformed.inputs().stream().anyMatch(input -> unavailable(index.get(input.id()), index, visited));
        }
        return false;
    }

    private static void source(Map<String, SourceArtifactRevision> index, SyntaxSourceOwner owner) {
        SourceArtifactRevision source = index.get(owner.resourcePath());
        ModelChecks.require(source != null && source.resourceSha256().equals(owner.resourceSha256()),
                "Evidence source artifact fingerprint mismatch");
    }

    private static void originalOwner(
            MechanicsOwnerReference effectiveOwner, String source, MechanicsOrigin origin,
            MechanicsOwnerReference authoredOwner) {
        if (origin == MechanicsOrigin.AUTHORED) {
            ModelChecks.require(effectiveOwner.source().identity().code().equals(source)
                    && (authoredOwner == null || effectiveOwner.equals(authoredOwner)), "Authored evidence owner mismatch");
        }
    }
}
