package com.thedndpub.data.mechanics.experimental;

import com.thedndpub.data.mechanics.model.AttackRollMechanic;
import com.thedndpub.data.mechanics.model.AttackRollOutcome;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.MechanicsHashes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Resolves explicit authored attack outcome clauses to damage mechanics. */
public final class AttackDamageOutcomeStrategy implements MechanicRelationshipStrategy {
    private static final Pattern OUTCOME = Pattern.compile(
            "(?i)\\b(on (?:a )?hit|if (?:the )?attack hits|if it hits|when (?:the )?attack hits|"
                    + "on (?:a )?miss|if (?:the )?attack misses|if it misses|when (?:the )?attack misses)\\b");
    private static final Pattern CHAIN = Pattern.compile(
            "(?i)\\b(?:another|new|additional) (?:spell )?attack\\b|\\battack again\\b|\\bleap(?:s|ing)? to another target\\b");

    @Override public String strategyId() { return "attack-damage-outcome"; }
    @Override public String version() { return "1"; }
    @Override public Set<MechanicRelationshipType> supportedTypes() {
        return Set.of(MechanicRelationshipType.ATTACK_DAMAGE_OUTCOME);
    }

    @Override
    public RelationshipResolutionResult resolve(List<ExtractedMechanic> mechanics,
            MechanicInterpretationContext context) {
        List<ExtractedMechanic> attacks = mechanics.stream()
                .filter(item -> item.payload() instanceof AttackRollMechanic)
                .sorted(Comparator.comparing(ExtractedMechanic::id)).toList();
        List<ExtractedMechanic> damages = mechanics.stream()
                .filter(item -> item.payload() instanceof DamageMechanic)
                .sorted(Comparator.comparing(ExtractedMechanic::id)).toList();
        Map<Pair, List<AttackDamageOutcomeRule>> assignments = new LinkedHashMap<>();
        List<ExperimentalDiagnosticDto> diagnostics = new ArrayList<>();

        for (SourceFragment fragment : context.fragments().values().stream()
                .filter(item -> item.kind() == SourceFragment.Kind.AUTHORED_TEXT)
                .sorted(Comparator.comparing(SourceFragment::pointer)).toList()) {
            Matcher outcomeMatcher = OUTCOME.matcher(fragment.text());
            List<OutcomePhrase> outcomes = new ArrayList<>();
            while (outcomeMatcher.find()) {
                String wording = outcomeMatcher.group(1).toLowerCase(Locale.ROOT);
                AttackRollOutcome outcome = wording.contains("miss") ? AttackRollOutcome.MISS : AttackRollOutcome.HIT;
                DamageApplicationBehavior behavior = behavior(fragment.text(), outcomeMatcher.start(), outcomeMatcher.end());
                outcomes.add(new OutcomePhrase(outcomeMatcher.start(), outcomeMatcher.end(), outcome, behavior));
            }
            if (CHAIN.matcher(fragment.text()).find()) {
                diagnostics.add(diagnostic("CONDITIONAL_ATTACK_CHAIN_UNSUPPORTED", "INFO", null, fragment,
                        "The source describes a conditional later attack; this step records only direct attack-to-damage outcomes."));
            }
            if (outcomes.isEmpty()) {
                boolean localAttack = attacks.stream().anyMatch(item -> item.sourceFragmentId().equals(fragment.id()));
                boolean localDamage = damages.stream().anyMatch(item -> item.sourceFragmentId().equals(fragment.id()));
                if (localAttack && localDamage) {
                    diagnostics.add(diagnostic("ATTACK_DAMAGE_CO_OCCURRENCE_ONLY", "INFO", null, fragment,
                            "Attack and damage share source text, but no explicit HIT or MISS clause links them."));
                }
                continue;
            }
            if (outcomes.stream().anyMatch(item -> item.behavior() == null)) {
                diagnostics.add(diagnostic("ATTACK_DAMAGE_OUTCOME_UNRESOLVED", "WARNING", null, fragment,
                        "An attack outcome is stated, but damage application for that outcome is not explicit."));
                continue;
            }

            List<ExtractedMechanic> localAttacks = attacks.stream()
                    .filter(item -> item.sourceFragmentId().equals(fragment.id())).toList();
            List<ExtractedMechanic> candidates = !localAttacks.isEmpty() ? localAttacks : attacks;
            if (candidates.size() != 1) {
                diagnostics.add(diagnostic("AMBIGUOUS_ATTACK_DAMAGE_PAIRING", "WARNING", null, fragment,
                        "The explicit attack outcome cannot be assigned to exactly one attack mechanic."));
                continue;
            }
            ExtractedMechanic attack = candidates.getFirst();
            List<ExtractedMechanic> localDamages = damages.stream()
                    .filter(item -> item.sourceFragmentId().equals(fragment.id())).toList();
            Map<String, List<AttackDamageOutcomeRule>> perDamage = new LinkedHashMap<>();
            for (int index = 0; index < outcomes.size(); index++) {
                OutcomePhrase outcome = outcomes.get(index);
                int start = previousBoundary(fragment.text(), outcome.start());
                int end = nextBoundary(fragment.text(), outcome.end());
                for (int later = index + 1; later < outcomes.size(); later++) {
                    if (outcomes.get(later).start() < end) end = outcomes.get(later).start();
                }
                int clauseEnd = end;
                List<ExtractedMechanic> clauseDamages = localDamages.stream()
                        .filter(item -> item.textStart() != null && item.textStart() >= start
                                && item.textStart() < clauseEnd
                                && !isSeparateActivation(item, fragment.text()))
                        .toList();
                String outcomeClause = fragment.text().substring(outcome.start(), clauseEnd).toLowerCase(Locale.ROOT);
                if (clauseDamages.isEmpty() && (outcomeClause.contains("half")
                        || outcomeClause.contains("no damage"))) {
                    clauseDamages = localDamages.stream()
                            .filter(item -> item.textStart() != null && item.textStart() < outcome.start()
                                    && (outcomeClause.contains("initial damage") || item.textStart() >= start)
                                    && !isSeparateActivation(item, fragment.text()))
                            .toList();
                }
                if (clauseDamages.isEmpty()) {
                    diagnostics.add(diagnostic("ATTACK_DAMAGE_TARGET_NOT_EXTRACTED", "WARNING", attack.id(), fragment,
                            "An explicit attack outcome was found, but its damage expression was not extracted from the clause."));
                    continue;
                }
                for (ExtractedMechanic damage : clauseDamages) {
                    DamageApplicationBehavior application = behaviorForDamage(
                            outcome, damage, fragment.text(), outcomeClause);
                    perDamage.computeIfAbsent(damage.id(), ignored -> new ArrayList<>())
                            .add(new AttackDamageOutcomeRule(outcome.outcome(), application));
                }
            }
            for (ExtractedMechanic damage : localDamages) {
                if (isSeparateActivation(damage, fragment.text())) {
                    diagnostics.add(diagnostic("ATTACK_DAMAGE_SEPARATE_ACTIVATION_UNSUPPORTED", "INFO",
                            damage.id(), fragment,
                            "This damage belongs to a later action or activation and is not linked to the initial attack."));
                    continue;
                }
                if (isDelayedDamage(damage, fragmentText(context, damage.sourceFragmentId()))) {
                    SourceFragment damageFragment = context.fragments().get(damage.sourceFragmentId());
                    diagnostics.add(diagnostic("ATTACK_DAMAGE_DELAYED_TIMING_UNMODELED", "WARNING", damage.id(),
                            damageFragment,
                            "The attack outcome relationship is retained, but the damage timing qualifier is not represented by this relationship family."));
                }
                List<AttackDamageOutcomeRule> sourceRules = perDamage.getOrDefault(damage.id(), List.of()).stream()
                        .distinct().toList();
                boolean conflicting = sourceRules.stream().map(AttackDamageOutcomeRule::outcome).distinct()
                        .anyMatch(outcome -> sourceRules.stream().filter(rule -> rule.outcome() == outcome)
                                .map(AttackDamageOutcomeRule::application).distinct().count() > 1);
                if (conflicting) {
                    diagnostics.add(diagnostic("CONFLICTING_ATTACK_DAMAGE_OUTCOMES", "WARNING", damage.id(), fragment,
                            "The source assigns conflicting damage application behavior to the same attack outcome."));
                    continue;
                }
                List<AttackDamageOutcomeRule> rules = sourceRules.stream()
                        .sorted(Comparator.comparing(rule -> rule.outcome().name())).toList();
                if (!rules.isEmpty()) assignments.put(new Pair(attack, damage), rules);
            }
        }

        List<ExperimentalMechanicRelationshipDto> relationships = new ArrayList<>();
        for (Map.Entry<Pair, List<AttackDamageOutcomeRule>> assignment : assignments.entrySet()) {
            Pair pair = assignment.getKey();
            String attackEvidence = context.evidenceIdsByFragment().get(pair.attack().sourceFragmentId());
            String damageEvidence = context.evidenceIdsByFragment().get(pair.damage().sourceFragmentId());
            SourceFragment source = context.fragments().get(pair.damage().sourceFragmentId());
            if (attackEvidence == null || damageEvidence == null) {
                diagnostics.add(diagnostic("ATTACK_DAMAGE_EVIDENCE_MISSING", "ERROR", pair.attack().id(), source,
                        "Relationship source evidence was not indexed for both endpoints."));
                continue;
            }
            List<String> evidenceIds = attackEvidence.equals(damageEvidence)
                    ? List.of(attackEvidence) : List.of(attackEvidence, damageEvidence);
            String id = "rel-" + MechanicsHashes.sha256(context.ownerId().value() + "|"
                    + context.sourceRevision().resourceSha256() + "|" + strategyId() + "|"
                    + pair.attack().id() + "|" + pair.damage().id() + "|" + assignment.getValue())
                    .substring(0, 20);
            relationships.add(new ExperimentalMechanicRelationshipDto(id,
                    MechanicRelationshipType.ATTACK_DAMAGE_OUTCOME, context.ownerId(), context.sourceRevision(),
                    pair.attack().id(), pair.damage().id(), List.of(), evidenceIds, assignment.getValue()));
        }
        return new RelationshipResolutionResult(relationships.stream()
                .sorted(Comparator.comparing(ExperimentalMechanicRelationshipDto::id)).toList(),
                diagnostics.stream().sorted(Comparator.comparing(ExperimentalDiagnosticDto::code)
                        .thenComparing(item -> item.pointer() == null ? "" : item.pointer())).toList());
    }

    private static DamageApplicationBehavior behavior(String text, int start, int end) {
        int clauseStart = previousBoundary(text, start);
        int clauseEnd = nextBoundary(text, end);
        String clause = text.substring(clauseStart, clauseEnd).toLowerCase(Locale.ROOT);
        if (clause.contains("half as much damage") || clause.contains("half damage")) {
            return DamageApplicationBehavior.HALF_DAMAGE;
        }
        if (clause.contains("no damage") || clause.contains("takes no damage")) {
            return DamageApplicationBehavior.NO_DAMAGE;
        }
        if (clause.contains("takes") || clause.contains("deals") || clause.contains("damage")) {
            return DamageApplicationBehavior.FULL_DAMAGE;
        }
        return null;
    }

    private static DamageApplicationBehavior behaviorForDamage(
            OutcomePhrase outcome, ExtractedMechanic damage, String text, String outcomeClause) {
        if (outcome.outcome() == AttackRollOutcome.MISS && outcomeClause.contains("initial damage")) {
            if (isDelayedDamage(damage, text) && outcomeClause.contains("no damage")
                    && outcomeClause.contains("end")) {
                return DamageApplicationBehavior.NO_DAMAGE;
            }
            if (!isDelayedDamage(damage, text) && outcomeClause.contains("half")) {
                return DamageApplicationBehavior.HALF_DAMAGE;
            }
        }
        return outcome.behavior();
    }

    private static boolean isDelayedDamage(ExtractedMechanic damage, String text) {
        if (damage.textEnd() == null) return false;
        Matcher nextDamage = Pattern.compile("(?i)\\{@damage\\s+[^}]+}").matcher(text);
        int end = nextBoundary(text, damage.textEnd());
        if (nextDamage.find(damage.textEnd())) end = Math.min(end, nextDamage.start());
        String following = text.substring(damage.textEnd(), end).toLowerCase(Locale.ROOT);
        return following.contains("damage at the end") || following.contains("damage at the start")
                || following.contains("damage later") || following.contains("damage on its next turn")
                || following.contains("damage on the next turn");
    }

    private static boolean isSeparateActivation(ExtractedMechanic damage, String text) {
        if (damage.textStart() == null) return false;
        int start = previousBoundary(text, damage.textStart());
        String prefix = text.substring(start, damage.textStart()).toLowerCase(Locale.ROOT);
        return prefix.matches("(?s).*(?:on each (?:of )?your turns?|on your subsequent turns?|"
                + "use your (?:action|bonus action)|as a (?:bonus )?action|as a reaction|"
                + "on each turn, you can use).*?");
    }

    private static String fragmentText(MechanicInterpretationContext context, String fragmentId) {
        SourceFragment fragment = context.fragments().get(fragmentId);
        return fragment == null ? "" : fragment.text();
    }

    private static int previousBoundary(String text, int position) {
        int boundary = -1;
        for (char character : new char[]{'.', ';', '\n'}) {
            boundary = Math.max(boundary, text.lastIndexOf(character, Math.max(0, position - 1)));
        }
        return boundary + 1;
    }

    private static int nextBoundary(String text, int position) {
        int boundary = text.length();
        for (char character : new char[]{'.', ';', '\n'}) {
            int found = text.indexOf(character, position);
            if (found >= 0 && found < boundary) boundary = found;
        }
        return boundary;
    }

    private static ExperimentalDiagnosticDto diagnostic(String code, String severity, String mechanicId,
            SourceFragment fragment, String explanation) {
        return new ExperimentalDiagnosticDto(code, severity, mechanicId, fragment.pointer(), explanation);
    }

    private record OutcomePhrase(int start, int end, AttackRollOutcome outcome,
            DamageApplicationBehavior behavior) { }
    private record Pair(ExtractedMechanic attack, ExtractedMechanic damage) { }
}
