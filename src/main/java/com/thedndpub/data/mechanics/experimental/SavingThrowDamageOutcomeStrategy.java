package com.thedndpub.data.mechanics.experimental;

import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.FixedAbility;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.SavingThrowRequirementMechanic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Resolves explicit, unambiguous authored save-result clauses to extracted damage effects. */
public final class SavingThrowDamageOutcomeStrategy implements MechanicRelationshipStrategy {
    private static final Pattern DAMAGE_OUTCOME = Pattern.compile(
            "(?i)must succeed on (?:a )?(strength|dexterity|constitution|intelligence|wisdom|charisma)"
                    + " saving throw or take|on (?:a )?failed (?:saving throw|save|one)|"
                    + "on (?:a )?successful (?:saving throw|save|one)");
    private static final Pattern SAVE_ABILITY = Pattern.compile(
            "(?i)\\b(strength|dexterity|constitution|intelligence|wisdom|charisma) saving throw\\b");
    private static final Pattern DAMAGE_TAG = Pattern.compile("(?i)\\{@damage\\s+([^}]+)}");
    private static final Pattern DICE = Pattern.compile("(?i)([1-9][0-9]*)d([1-9][0-9]*)(?:\\s*([+-])\\s*([0-9]+))?");
    private static final Pattern DAMAGE_TYPE = Pattern.compile("(?i)^\\s*(bludgeoning|piercing|slashing|acid|cold|"
            + "fire|force|lightning|necrotic|poison|psychic|radiant|thunder)\\s+damage\\b");

    @Override
    public String strategyId() {
        return "saving-throw-damage-outcome";
    }

    @Override
    public String version() {
        return "1";
    }

    @Override
    public Set<MechanicRelationshipType> supportedTypes() {
        return Set.of(MechanicRelationshipType.SAVING_THROW_DAMAGE_OUTCOME);
    }

    @Override
    public RelationshipResolutionResult resolve(
            List<ExtractedMechanic> mechanics,
            MechanicInterpretationContext context) {
        List<ExtractedMechanic> saves = mechanics.stream()
                .filter(item -> item.payload() instanceof SavingThrowRequirementMechanic)
                .sorted(Comparator.comparing(ExtractedMechanic::id)).toList();
        List<ExtractedMechanic> damages = mechanics.stream()
                .filter(item -> item.payload() instanceof DamageMechanic)
                .sorted(Comparator.comparing(ExtractedMechanic::id)).toList();
        Map<PairKey, List<SaveDamageOutcomeRule>> assignments = new HashMap<>();
        List<ExperimentalDiagnosticDto> diagnostics = new ArrayList<>();

        for (SourceFragment fragment : context.fragments().values().stream()
                .filter(item -> item.kind() == SourceFragment.Kind.AUTHORED_TEXT)
                .sorted(Comparator.comparing(SourceFragment::pointer)).toList()) {
            List<OutcomePhrase> phrases = outcomePhrases(fragment.text());
            List<ExtractedMechanic> localDamages = damages.stream()
                    .filter(item -> item.sourceFragmentId().equals(fragment.id())).toList();
            if (phrases.isEmpty()) {
                boolean coOccurs = saves.stream().anyMatch(item -> item.sourceFragmentId().equals(fragment.id()))
                        && !localDamages.isEmpty();
                if (coOccurs) {
                    diagnostics.add(diagnostic("SAVE_DAMAGE_CO_OCCURRENCE_ONLY", "INFO", null, fragment,
                            "A save and damage share source text, but no explicit save outcome clause establishes a relationship."));
                }
                continue;
            }
            if (phrases.stream().anyMatch(phrase -> phrase.rule() == null)) {
                diagnostics.add(diagnostic("SAVE_DAMAGE_OUTCOME_UNRESOLVED", "WARNING", null, fragment,
                        "A save outcome is mentioned, but the source does not specify how that outcome applies damage."));
                continue;
            }
            if (localDamages.isEmpty()) {
                diagnostics.add(diagnostic("SAVE_DAMAGE_TARGET_NOT_EXTRACTED", "WARNING", null, fragment,
                        "An explicit save outcome clause was found, but no damage mechanic was extracted from that source fragment."));
                continue;
            }

            List<ExtractedMechanic> eligibleSaves = eligibleSaves(saves, fragment, phrases);
            if (eligibleSaves.size() != 1) {
                diagnostics.add(diagnostic("AMBIGUOUS_SAVE_DAMAGE_PAIRING", "WARNING", null, fragment,
                        "An explicit damage outcome clause was found, but the governing saving throw is not unique."));
                continue;
            }
            ExtractedMechanic save = eligibleSaves.getFirst();
            Map<String, List<SaveDamageOutcomeRule>> rulesForDamage = new HashMap<>();
            for (OutcomePhrase phrase : phrases) {
                List<ExtractedMechanic> clauseDamages = damagesForPhrase(localDamages, fragment.text(), phrase, phrases);
                if (clauseDamages.isEmpty()) {
                    diagnostics.add(diagnostic("AMBIGUOUS_SAVE_DAMAGE_AMOUNT", "WARNING", save.id(), fragment,
                            "An outcome is authored, but its damage component cannot be associated unambiguously."));
                    continue;
                }
                for (ExtractedMechanic damage : clauseDamages) {
                    rulesForDamage.computeIfAbsent(damage.id(), ignored -> new ArrayList<>()).add(phrase.rule());
                }
            }
            for (ExtractedMechanic damage : localDamages) {
                List<SaveDamageOutcomeRule> rules = rulesForDamage.getOrDefault(damage.id(), List.of()).stream()
                        .distinct().sorted(Comparator.comparing(rule -> rule.outcome().name())).toList();
                if (rules.isEmpty()) {
                    continue;
                }
                assignments.put(new PairKey(save.id(), damage.id(), save.sourceFragmentId(),
                        damage.sourceFragmentId()), rules);
            }
        }

        List<ExperimentalMechanicRelationshipDto> relationships = new ArrayList<>();
        for (Map.Entry<PairKey, List<SaveDamageOutcomeRule>> assignment : assignments.entrySet()) {
            PairKey pair = assignment.getKey();
            String saveEvidenceId = context.evidenceIdsByFragment().get(pair.saveFragmentId());
            String damageEvidenceId = context.evidenceIdsByFragment().get(pair.damageFragmentId());
            SourceFragment fragment = context.fragments().get(pair.damageFragmentId());
            if (saveEvidenceId == null || damageEvidenceId == null) {
                diagnostics.add(diagnostic("SAVE_DAMAGE_EVIDENCE_MISSING", "ERROR", pair.saveId(), fragment,
                        "Relationship source evidence was not indexed for both endpoints."));
                continue;
            }
            String id = "rel-" + com.thedndpub.data.mechanics.model.MechanicsHashes.sha256(
                    context.ownerId().value() + "|" + context.sourceRevision().resourceSha256() + "|"
                            + strategyId() + "|" + pair.saveId() + "|" + pair.damageId() + "|"
                            + assignment.getValue()).substring(0, 20);
            relationships.add(new ExperimentalMechanicRelationshipDto(id,
                    MechanicRelationshipType.SAVING_THROW_DAMAGE_OUTCOME, context.ownerId(),
                    context.sourceRevision(), pair.saveId(), pair.damageId(), assignment.getValue(),
                    List.of(saveEvidenceId, damageEvidenceId)));
        }
        return new RelationshipResolutionResult(relationships.stream()
                .sorted(Comparator.comparing(ExperimentalMechanicRelationshipDto::id)).toList(),
                diagnostics.stream().sorted(Comparator.comparing(ExperimentalDiagnosticDto::code)
                        .thenComparing(item -> item.pointer() == null ? "" : item.pointer())).toList());
    }

    private static List<ExtractedMechanic> eligibleSaves(
            List<ExtractedMechanic> saves,
            SourceFragment fragment,
            List<OutcomePhrase> phrases) {
        List<ExtractedMechanic> local = saves.stream()
                .filter(item -> item.sourceFragmentId().equals(fragment.id())).toList();
        List<ExtractedMechanic> candidates = local.isEmpty() ? saves : local;
        List<String> explicitAbilities = phrases.stream().map(OutcomePhrase::ability).filter(java.util.Objects::nonNull)
                .distinct().toList();
        if (explicitAbilities.size() > 1) {
            return List.of();
        }
        if (!explicitAbilities.isEmpty()) {
            return candidates.stream().filter(item -> saveAbilityMatches(item, explicitAbilities.getFirst())).toList();
        }
        List<String> localAbilities = abilities(fragment.text());
        if (localAbilities.size() > 1) {
            return List.of();
        }
        if (!localAbilities.isEmpty()) {
            List<ExtractedMechanic> abilityMatches = candidates.stream()
                    .filter(item -> saveAbilityMatches(item, localAbilities.getFirst())).toList();
            if (!abilityMatches.isEmpty()) {
                return abilityMatches;
            }
        }
        return candidates;
    }

    private static List<ExtractedMechanic> damagesForPhrase(
            List<ExtractedMechanic> damages,
            String text,
            OutcomePhrase phrase,
            List<OutcomePhrase> phrases) {
        int clauseStart = previousBoundary(text, phrase.start());
        for (OutcomePhrase previous : phrases) {
            if (previous.end() <= phrase.start() && previous.end() > clauseStart) {
                clauseStart = previous.end();
            }
        }
        int clauseEnd = nextBoundary(text, phrase.end());
        if (clauseEnd < 0) {
            clauseEnd = text.length();
        }
        for (OutcomePhrase other : phrases) {
            if (other.start() > phrase.start() && other.start() < clauseEnd) {
                clauseEnd = other.start();
            }
        }
        int nextOutcome = phrases.stream().filter(other -> other.start() > phrase.start())
                .mapToInt(OutcomePhrase::start).min().orElse(clauseEnd);
        int afterOutcomeEnd = Math.min(clauseEnd, nextOutcome);
        List<DiceAmount> afterOutcome = damageAmounts(text.substring(phrase.end(), afterOutcomeEnd));
        if (!afterOutcome.isEmpty()) {
            return matchDamages(damages, afterOutcome);
        }
        List<DiceAmount> beforeOutcome = damageAmounts(text.substring(clauseStart, phrase.start()));
        if (!beforeOutcome.isEmpty()) {
            return matchDamages(damages, beforeOutcome);
        }
        if (phrase.rule().application() == DamageApplicationBehavior.NO_DAMAGE) {
            return damages;
        }
        if (phrase.rule().application() == DamageApplicationBehavior.HALF_DAMAGE) {
            List<DiceAmount> priorAmounts = damageAmounts(text.substring(previousBoundary(text, phrase.start()),
                    phrase.start()));
            if (!priorAmounts.isEmpty() || damages.size() > 1) {
                return damages;
            }
            if (damages.size() == 1) {
                return damages;
            }
        }
        if (damages.size() == 1) {
            return damages;
        }
        return List.of();
    }

    private static List<ExtractedMechanic> matchDamages(List<ExtractedMechanic> damages, List<DiceAmount> amounts) {
        List<DiceAmount> distinctAmounts = amounts.stream().distinct().toList();
        return damages.stream().filter(item -> item.payload() instanceof DamageMechanic damage
                && distinctAmounts.contains(damage.amount())).toList();
    }

    private static List<OutcomePhrase> outcomePhrases(String text) {
        Matcher matcher = DAMAGE_OUTCOME.matcher(text);
        List<OutcomePhrase> result = new ArrayList<>();
        while (matcher.find()) {
            String phrase = matcher.group();
            String ability = matcher.group(1) == null ? null : matcher.group(1).toUpperCase(Locale.ROOT);
            DamageApplicationBehavior application = behavior(text, matcher.start(), matcher.end(), phrase);
            SaveDamageOutcomeCondition outcome = phrase.toLowerCase(Locale.ROOT).contains("failed")
                    || phrase.toLowerCase(Locale.ROOT).contains("or take")
                    ? SaveDamageOutcomeCondition.FAILURE : SaveDamageOutcomeCondition.SUCCESS;
            SaveDamageOutcomeRule rule = application == null ? null : new SaveDamageOutcomeRule(outcome, application);
            result.add(new OutcomePhrase(matcher.start(), matcher.end(), ability, rule));
        }
        return result;
    }

    private static DamageApplicationBehavior behavior(String text, int start, int end, String phrase) {
        String lower = phrase.toLowerCase(Locale.ROOT);
        int clauseEnd = nextBoundary(text, end);
        if (clauseEnd < 0) {
            clauseEnd = text.length();
        }
        int precedingStart = previousBoundary(text, start);
        int nextOutcomeStart = -1;
        Matcher otherOutcomes = DAMAGE_OUTCOME.matcher(text);
        while (otherOutcomes.find()) {
            if (otherOutcomes.end() <= start) {
                precedingStart = Math.max(precedingStart, otherOutcomes.end());
            } else if (otherOutcomes.start() >= end) {
                nextOutcomeStart = otherOutcomes.start();
                clauseEnd = Math.min(clauseEnd, otherOutcomes.start());
                break;
            }
        }
        String clauseText = text.substring(start, clauseEnd);
        if (nextOutcomeStart >= 0) {
            int alternativeStart = clauseText.toLowerCase(Locale.ROOT).lastIndexOf(" or ");
            if (alternativeStart >= 0) {
                clauseText = clauseText.substring(0, alternativeStart);
            }
        }
        String clause = clauseText.toLowerCase(Locale.ROOT);
        String preceding = text.substring(precedingStart, start).toLowerCase(Locale.ROOT);
        if (clause.contains("half") || preceding.contains("half")) {
            return DamageApplicationBehavior.HALF_DAMAGE;
        }
        if (clause.contains("no damage") || preceding.contains("no damage")) {
            return DamageApplicationBehavior.NO_DAMAGE;
        }
        if (clause.contains("full damage") || preceding.contains("full damage")) {
            return DamageApplicationBehavior.FULL_DAMAGE;
        }
        if (DAMAGE_TAG.matcher(clause).find() || DAMAGE_TAG.matcher(preceding).find()
                || clause.contains("same damage") || preceding.contains("same damage")) {
            return DamageApplicationBehavior.FULL_DAMAGE;
        }
        if (lower.contains("failed") || lower.contains("or take")) {
            return DamageApplicationBehavior.FULL_DAMAGE;
        }
        return null;
    }

    private static List<DiceAmount> damageAmounts(String text) {
        Matcher tags = DAMAGE_TAG.matcher(text);
        List<DiceAmount> result = new ArrayList<>();
        while (tags.find()) {
            Matcher dice = DICE.matcher(tags.group(1).trim());
            if (!dice.matches()) {
                continue;
            }
            int modifier = dice.group(4) == null ? 0 : Integer.parseInt(dice.group(4));
            if ("-".equals(dice.group(3))) {
                modifier = -modifier;
            }
            result.add(new DiceAmount(Integer.parseInt(dice.group(1)), Integer.parseInt(dice.group(2)), modifier));
        }
        return result;
    }

    private static List<String> abilities(String text) {
        Matcher matcher = SAVE_ABILITY.matcher(text);
        List<String> result = new ArrayList<>();
        while (matcher.find()) {
            String ability = matcher.group(1).toUpperCase(Locale.ROOT);
            if (!result.contains(ability)) {
                result.add(ability);
            }
        }
        return result;
    }

    private static boolean saveAbilityMatches(ExtractedMechanic save, String authoredAbility) {
        if (!(save.payload() instanceof SavingThrowRequirementMechanic payload)
                || !(payload.ability() instanceof FixedAbility ability)) {
            return false;
        }
        return ability.ability().name().equals(authoredAbility);
    }

    private static int previousBoundary(String text, int position) {
        int boundary = -1;
        for (char character : new char[]{'.', ';', '\n'}) {
            boundary = Math.max(boundary, text.lastIndexOf(character, Math.max(0, position - 1)));
        }
        return boundary + 1;
    }

    private static int nextBoundary(String text, int position) {
        int boundary = -1;
        for (char character : new char[]{'.', ';', '\n'}) {
            int found = text.indexOf(character, position);
            if (found >= 0 && (boundary < 0 || found < boundary)) {
                boundary = found;
            }
        }
        return boundary;
    }

    private static ExperimentalDiagnosticDto diagnostic(
            String code, String severity, String mechanicId, SourceFragment fragment, String explanation) {
        return new ExperimentalDiagnosticDto(code, severity, mechanicId, fragment.pointer(), explanation);
    }

    private record OutcomePhrase(int start, int end, String ability, SaveDamageOutcomeRule rule) { }

    private record PairKey(String saveId, String damageId, String saveFragmentId, String damageFragmentId) { }
}
