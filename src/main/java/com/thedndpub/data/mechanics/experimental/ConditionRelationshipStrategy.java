package com.thedndpub.data.mechanics.experimental;

import com.thedndpub.data.mechanics.model.ConditionChangeMechanic;
import com.thedndpub.data.mechanics.model.ConditionChoiceMechanic;
import com.thedndpub.data.mechanics.model.ConditionOperation;
import com.thedndpub.data.mechanics.model.ConditionTerminationEventMechanic;
import com.thedndpub.data.mechanics.model.EffectDurationMechanic;
import com.thedndpub.data.mechanics.model.FixedAbility;
import com.thedndpub.data.mechanics.model.MechanicsHashes;
import com.thedndpub.data.mechanics.model.SaveOutcome;
import com.thedndpub.data.mechanics.model.SavingThrowRequirementMechanic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Resolves only explicit source links among conditions, saves, and their effect durations. */
public final class ConditionRelationshipStrategy implements MechanicRelationshipStrategy {
    private static final Pattern SAVE = Pattern.compile("(?i)\\b(?:saving throw|save)\\b");
    private static final Pattern SAVE_ABILITY = Pattern.compile(
            "(?i)\\b(strength|dexterity|constitution|intelligence|wisdom|charisma) saving throw\\b");
    private static final Pattern INITIAL_FAILURE = Pattern.compile(
            "(?i)\\bmust succeed on (?:a )?(?:strength|dexterity|constitution|intelligence|wisdom|charisma)"
                    + " saving throw or\\b|\\bif (?:the )?(?:target|creature|it) fails (?:the )?(?:saving throw|save)\\b|"
                    + "\\b(?:on|after) (?:a )?failed (?:saving throw|save)\\b|\\bif it fails\\b");
    private static final Pattern SUCCESS_PREVENTS = Pattern.compile(
            "(?i)\\b(?:on|if) (?:a )?successful (?:(?:strength|dexterity|constitution|intelligence|"
                    + "wisdom|charisma) )?(?:saving throw|save)\\b[^.;]{0,100}"
                    + "\\b(?:prevent|cannot|can't|does not become|is not)\\b|"
                    + "\\bmust succeed on (?:a )?(?:strength|dexterity|constitution|intelligence|wisdom|charisma)"
                    + " saving throw or be\\b");
    private static final Pattern REPEAT = Pattern.compile(
            "(?i)\\b(?:repeat|repeats|repeatedly make|can make another)\\b[^.;]{0,100}"
                    + "\\b(?:saving throw|save)\\b|\\bat the end of each of its turns\\b[^.;]{0,100}"
                    + "\\b(?:saving throw|save)\\b");
    private static final Pattern REPEAT_END = Pattern.compile(
            "(?i)\\b(?:ending|ends|the effect ends|the spell ends|no longer)\\b[^.;]{0,100}"
                    + "\\b(?:on a )?success\\b|\\bon a successful (?:saving throw|save)\\b[^.;]{0,100}"
                    + "\\b(?:end|ends|ending|no longer)\\b|\\bon a success\\b[^.;]{0,60}"
                    + "\\b(?:the )?(?:spell|effect) ends\\b");
    private static final Pattern FAILURE_MAINTAINS = Pattern.compile(
            "(?i)\\b(?:on|if) (?:a )?failed (?:saving throw|save)\\b[^.;]{0,100}"
                    + "\\b(?:remains|continues|still)\\b");

    @Override public String strategyId() { return "condition-lifecycle-relationships"; }
    @Override public String version() { return "1"; }
    @Override public Set<MechanicRelationshipType> supportedTypes() {
        return Set.of(MechanicRelationshipType.SAVE_CONDITION_OUTCOME,
                MechanicRelationshipType.CONDITION_DURATION, MechanicRelationshipType.CONDITION_TERMINATION);
    }

    @Override
    public RelationshipResolutionResult resolve(List<ExtractedMechanic> mechanics,
            MechanicInterpretationContext context) {
        List<ExperimentalMechanicRelationshipDto> relationships = new ArrayList<>();
        List<ExperimentalDiagnosticDto> diagnostics = new ArrayList<>();
        List<ExtractedMechanic> saves = mechanics.stream()
                .filter(item -> item.payload() instanceof SavingThrowRequirementMechanic)
                .sorted(Comparator.comparing(ExtractedMechanic::id)).toList();
        List<ExtractedMechanic> conditions = mechanics.stream().filter(item -> isCondition(item.payload()))
                .sorted(Comparator.comparing(ExtractedMechanic::id)).toList();
        List<ExtractedMechanic> durations = mechanics.stream()
                .filter(item -> item.payload() instanceof EffectDurationMechanic)
                .sorted(Comparator.comparing(ExtractedMechanic::id)).toList();
        List<ExtractedMechanic> terminationEvents = mechanics.stream()
                .filter(item -> item.payload() instanceof ConditionTerminationEventMechanic)
                .sorted(Comparator.comparing(ExtractedMechanic::id)).toList();

        Map<Pair, List<ConditionSaveOutcomeRule>> outcomeRules = new LinkedHashMap<>();
        Map<Pair, ConditionTerminationRule> terminationRules = new LinkedHashMap<>();
        for (SourceFragment fragment : context.fragments().values().stream()
                .filter(item -> item.kind() == SourceFragment.Kind.AUTHORED_TEXT)
                .sorted(Comparator.comparing(SourceFragment::pointer)).toList()) {
            List<ExtractedMechanic> localConditions = conditions.stream()
                    .filter(item -> item.sourceFragmentId().equals(fragment.id()))
                    .filter(item -> isOutcomeCondition(item.payload())).toList();
            List<ExtractedMechanic> localSaves = saves.stream()
                    .filter(item -> item.sourceFragmentId().equals(fragment.id())).toList();
            boolean hasOutcome = INITIAL_FAILURE.matcher(fragment.text()).find()
                    || SUCCESS_PREVENTS.matcher(fragment.text()).find()
                    || FAILURE_MAINTAINS.matcher(fragment.text()).find();
            boolean repeats = REPEAT.matcher(fragment.text()).find();
            boolean repeatsAndEnds = repeats && REPEAT_END.matcher(fragment.text()).find();

            if (!hasOutcome && !repeatsAndEnds) continue;
            if (localSaves.isEmpty() && fragment.text().matches("(?is).*\\b(?:saving throw|save)\\b.*")
                    && saves.size() == 1) {
                localSaves = saves;
            }
            if (localConditions.isEmpty() && conditions.size() == 1
                    && fragment.text().matches("(?is).*\\b(?:condition|blinded|charmed|deafened|exhaustion|"
                    + "frightened|grappled|incapacitated|invisible|paralyzed|petrified|poisoned|prone|restrained|"
                    + "stunned|unconscious)\\b.*")) {
                localConditions = conditions;
            }
            if (localSaves.size() != 1 || localConditions.size() != 1) {
                diagnostics.add(diagnostic("CONDITION_SAVE_PAIRING_AMBIGUOUS", fragment,
                        "Source-backed save/condition wording was found, but its save or condition mechanic is not unique."));
                continue;
            }
            ExtractedMechanic save = localSaves.getFirst();
            ExtractedMechanic condition = localConditions.getFirst();
            if (!explicitAbilityMatches(fragment.text(), save.payload())) {
                diagnostics.add(diagnostic("CONDITION_SAVE_ABILITY_UNRESOLVED", fragment,
                        "The source mentions a save outcome, but its ability cannot be tied to the extracted saving throw."));
                continue;
            }
            Pair pair = new Pair(save, condition);
            if (INITIAL_FAILURE.matcher(fragment.text()).find()) {
                ConditionSaveEffect effect = conditionOperation(condition.payload()) == ConditionOperation.PREVENT
                        ? ConditionSaveEffect.PREVENT_APPLICATION : ConditionSaveEffect.APPLY;
                outcomeRules.computeIfAbsent(pair, ignored -> new ArrayList<>()).add(
                        new ConditionSaveOutcomeRule(SaveOutcome.FAILURE, effect, ConditionSaveTiming.INITIAL));
            }
            if (SUCCESS_PREVENTS.matcher(fragment.text()).find()) {
                outcomeRules.computeIfAbsent(pair, ignored -> new ArrayList<>()).add(
                        new ConditionSaveOutcomeRule(SaveOutcome.SUCCESS,
                                ConditionSaveEffect.PREVENT_APPLICATION, ConditionSaveTiming.INITIAL));
            }
            if (FAILURE_MAINTAINS.matcher(fragment.text()).find()) {
                outcomeRules.computeIfAbsent(pair, ignored -> new ArrayList<>()).add(
                        new ConditionSaveOutcomeRule(SaveOutcome.FAILURE,
                                ConditionSaveEffect.MAINTAIN, ConditionSaveTiming.REPEATED));
            }
            if (repeatsAndEnds) {
                terminationRules.put(pair, new ConditionTerminationRule(
                        ConditionTerminationTrigger.REPEATED_SAVE_SUCCESS,
                        com.thedndpub.data.mechanics.model.RepeatSaveTiming.END_OF_EACH_TARGET_TURN));
            }
        }

        for (Map.Entry<Pair, List<ConditionSaveOutcomeRule>> entry : outcomeRules.entrySet()) {
            addRelationship(relationships, diagnostics, context, entry.getKey(),
                    MechanicRelationshipType.SAVE_CONDITION_OUTCOME,
                    entry.getValue().stream().distinct().sorted(Comparator
                            .comparing((ConditionSaveOutcomeRule rule) -> rule.timing().name())
                            .thenComparing(rule -> rule.outcome().name())
                            .thenComparing(rule -> rule.effect().name())).toList(), null);
        }
        for (Map.Entry<Pair, ConditionTerminationRule> entry : terminationRules.entrySet()) {
            addRelationship(relationships, diagnostics, context, entry.getKey(),
                    MechanicRelationshipType.CONDITION_TERMINATION, List.of(), entry.getValue());
        }

        for (ExtractedMechanic event : terminationEvents) {
            SourceFragment eventFragment = context.fragments().get(event.sourceFragmentId());
            List<ExtractedMechanic> linkedConditions = conditions.stream().filter(condition -> {
                SourceFragment conditionFragment = context.fragments().get(condition.sourceFragmentId());
                return explicitlyReferencesCondition(eventFragment.text())
                        && mentionsCondition(eventFragment.text(), condition.payload())
                        && sharesEntryScope(eventFragment, conditionFragment);
            }).toList();
            if (linkedConditions.size() != 1) {
                diagnostics.add(diagnostic("CONDITION_TERMINATION_PAIRING_AMBIGUOUS", eventFragment,
                        "An explicit action-based ending event does not identify exactly one condition in its source scope."));
                continue;
            }
            addRelationship(relationships, diagnostics, context, new Pair(linkedConditions.getFirst(), event),
                    MechanicRelationshipType.CONDITION_TERMINATION, List.of(),
                    new ConditionTerminationRule(ConditionTerminationTrigger.ACTION_SUCCESSFUL_CHECK, null));
        }

        for (ExtractedMechanic condition : conditions.stream().filter(item -> isOutcomeCondition(item.payload())).toList()) {
            List<ExtractedMechanic> localDurations = durations.stream()
                    .filter(item -> item.sourceFragmentId().equals(condition.sourceFragmentId())).toList();
            SourceFragment conditionFragment = context.fragments().get(condition.sourceFragmentId());
            SourceFragment durationFragment = conditionFragment;
            if (localDurations.isEmpty()) {
                List<ExtractedMechanic> referenced = durations.stream().filter(item -> {
                    SourceFragment candidateFragment = context.fragments().get(item.sourceFragmentId());
                    return candidateFragment != null && !candidateFragment.id().equals(conditionFragment.id())
                            && explicitlyReferencesCondition(candidateFragment.text())
                            && sharesEntryScope(conditionFragment, candidateFragment);
                }).toList();
                if (referenced.size() == 1 && conditions.stream().filter(candidate -> isOutcomeCondition(candidate.payload())
                        && sharesEntryScope(conditionFragment,
                                context.fragments().get(candidate.sourceFragmentId()))).count() == 1) {
                    localDurations = referenced;
                    durationFragment = context.fragments().get(referenced.getFirst().sourceFragmentId());
                } else if (!referenced.isEmpty()) {
                    diagnostics.add(diagnostic("CONDITION_DURATION_AMBIGUOUS", conditionFragment,
                            "A sibling duration clause refers to a condition, but the source-scope pairing is not unique."));
                }
            }
            if (localDurations.size() > 1) {
                diagnostics.add(diagnostic("CONDITION_DURATION_AMBIGUOUS", conditionFragment,
                        "More than one duration mechanic shares a condition fragment; no duration relationship was inferred."));
                continue;
            }
            if (localDurations.size() == 1 && explicitlyTiedDuration(durationFragment.text())) {
                addRelationship(relationships, diagnostics, context, new Pair(condition, localDurations.getFirst()),
                        MechanicRelationshipType.CONDITION_DURATION, List.of(), null);
                if (isSpellEndDuration(localDurations.getFirst().payload())) {
                    addRelationship(relationships, diagnostics, context,
                            new Pair(condition, localDurations.getFirst()),
                            MechanicRelationshipType.CONDITION_TERMINATION, List.of(),
                            new ConditionTerminationRule(ConditionTerminationTrigger.SPELL_DURATION_ENDS, null));
                }
            }
        }

        return new RelationshipResolutionResult(relationships.stream()
                .sorted(Comparator.comparing(ExperimentalMechanicRelationshipDto::id)).toList(),
                diagnostics.stream().sorted(Comparator.comparing(ExperimentalDiagnosticDto::code)
                        .thenComparing(item -> item.pointer() == null ? "" : item.pointer())).toList());
    }

    private static void addRelationship(List<ExperimentalMechanicRelationshipDto> output,
            List<ExperimentalDiagnosticDto> diagnostics, MechanicInterpretationContext context, Pair pair,
            MechanicRelationshipType type, List<ConditionSaveOutcomeRule> outcomes,
            ConditionTerminationRule terminationRule) {
        String sourceFragment = pair.first().sourceFragmentId();
        String targetFragment = pair.second().sourceFragmentId();
        String firstEvidence = context.evidenceIdsByFragment().get(sourceFragment);
        String secondEvidence = context.evidenceIdsByFragment().get(targetFragment);
        SourceFragment fragment = context.fragments().get(targetFragment);
        if (firstEvidence == null || secondEvidence == null) {
            diagnostics.add(diagnostic("CONDITION_RELATIONSHIP_EVIDENCE_MISSING", fragment,
                    "Condition relationship evidence was not indexed for both mechanics."));
            return;
        }
        String id = "rel-" + MechanicsHashes.sha256(context.ownerId().value() + "|"
                + context.sourceRevision().resourceSha256() + "|" + type + "|"
                + pair.first().id() + "|" + pair.second().id() + "|" + outcomes + "|" + terminationRule)
                .substring(0, 20);
        output.add(new ExperimentalMechanicRelationshipDto(id, type, context.ownerId(), context.sourceRevision(),
                pair.first().id(), pair.second().id(), List.of(),
                firstEvidence.equals(secondEvidence) ? List.of(firstEvidence) : List.of(firstEvidence, secondEvidence),
                List.of(), null, outcomes, terminationRule));
    }

    private static boolean explicitAbilityMatches(String text, Object payload) {
        List<String> abilities = new ArrayList<>();
        Matcher matcher = SAVE_ABILITY.matcher(text);
        while (matcher.find()) abilities.add(matcher.group(1));
        if (abilities.isEmpty()) return text.matches("(?is).*\\b(?:failed|successful) (?:saving throw|save)\\b.*")
                && payload instanceof SavingThrowRequirementMechanic;
        if (!(payload instanceof SavingThrowRequirementMechanic save)
                || !(save.ability() instanceof FixedAbility fixed)) return false;
        return abilities.stream().map(String::toUpperCase).distinct().allMatch(fixed.ability().name()::equals);
    }

    private static boolean explicitlyTiedDuration(String text) {
        return text.matches("(?is).*(?:for the duration|until the spell ends|for\\s+(?:up to\\s+)?\\d+\\s+"
                + "(?:round|minute|hour|day)s?|lasts? for\\s+(?:up to\\s+)?\\d+\\s+"
                + "(?:round|minute|hour|day)s?|until the end of (?:its|the target's|your) next turn).*");
    }

    private static boolean explicitlyReferencesCondition(String text) {
        return text.matches("(?is).*(?:\\bcondition\\b|\\b(?:blinded|charmed|deafened|exhaustion|frightened|"
                + "grappled|incapacitated|invisible|paralyzed|petrified|poisoned|prone|restrained|stunned|"
                + "unconscious)\\b).*");
    }

    private static boolean mentionsCondition(String text, Object payload) {
        List<String> names = new ArrayList<>();
        if (payload instanceof ConditionChangeMechanic change) {
            names.add(change.condition().name().toLowerCase(java.util.Locale.ROOT));
        } else if (payload instanceof ConditionChoiceMechanic choice) {
            choice.alternatives().forEach(item -> names.add(item.name().toLowerCase(java.util.Locale.ROOT)));
        }
        return names.stream().anyMatch(name -> Pattern.compile("(?i)\\b" + Pattern.quote(name) + "\\b")
                .matcher(text).find());
    }

    private static boolean sharesEntryScope(SourceFragment left, SourceFragment right) {
        if (left == null || right == null) return false;
        int shared = 0;
        while (shared < left.hierarchy().size() && shared < right.hierarchy().size()
                && left.hierarchy().get(shared).equals(right.hierarchy().get(shared))) shared++;
        return shared > 0;
    }

    private static boolean isSpellEndDuration(Object payload) {
        return payload instanceof EffectDurationMechanic duration
                && duration.specification() instanceof com.thedndpub.data.mechanics.model.DeclaredSpellDurationReference;
    }

    private static boolean isCondition(Object payload) {
        return payload instanceof ConditionChangeMechanic || payload instanceof ConditionChoiceMechanic;
    }

    private static boolean isOutcomeCondition(Object payload) {
        return isCondition(payload) && (conditionOperation(payload) == ConditionOperation.APPLY
                || conditionOperation(payload) == ConditionOperation.PREVENT);
    }

    private static ConditionOperation conditionOperation(Object payload) {
        if (payload instanceof ConditionChangeMechanic change) return change.operation();
        if (payload instanceof ConditionChoiceMechanic choice) return choice.operation();
        return null;
    }

    private static ExperimentalDiagnosticDto diagnostic(String code, SourceFragment fragment, String text) {
        return new ExperimentalDiagnosticDto(code, "WARNING", null, fragment.pointer(), text);
    }

    private record Pair(ExtractedMechanic first, ExtractedMechanic second) { }
}
