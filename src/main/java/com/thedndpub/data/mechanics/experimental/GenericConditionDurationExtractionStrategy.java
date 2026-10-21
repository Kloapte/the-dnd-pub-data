package com.thedndpub.data.mechanics.experimental;

import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.mechanics.model.DeclaredSpellDurationReference;
import com.thedndpub.data.mechanics.model.DurationUnit;
import com.thedndpub.data.mechanics.model.EffectDurationMechanic;
import com.thedndpub.data.mechanics.model.FixedDuration;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.TurnActor;
import com.thedndpub.data.mechanics.model.TurnBoundary;
import com.thedndpub.data.mechanics.model.UntilTurnBoundary;
import com.thedndpub.data.mechanics.model.UnknownSubject;
import com.thedndpub.data.mechanics.model.UnknownValueReason;
import com.thedndpub.data.mechanics.model.SpellTargetSubject;
import com.thedndpub.data.mechanics.model.SubjectReference;
import com.thedndpub.data.mechanics.model.TargetScope;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Extracts a duration mechanic only when authored prose ties it to a named condition. */
public final class GenericConditionDurationExtractionStrategy implements MechanicExtractionStrategy {
    private static final String CONDITION_NAMES = "blinded|charmed|deafened|exhaustion|frightened|grappled|"
            + "incapacitated|invisible|paralyzed|petrified|poisoned|prone|restrained|stunned|unconscious";
    private static final Pattern CONDITION = Pattern.compile("(?i)\\b(?:condition|conditions|" + CONDITION_NAMES + ")\\b");
    private static final Pattern SPELL_END = Pattern.compile(
            "(?i)\\b(?:for the duration(?: of the spell)?|until the spell ends)\\b");
    private static final Pattern TURN_END = Pattern.compile(
            "(?i)\\buntil the end of (its|the target's|your) next turn\\b");
    private static final Pattern FIXED = Pattern.compile(
            "(?i)\\b(?:for|lasts? for)\\s+(up to\\s+)?(\\d+)\\s+(round|minute|hour|day)s?\\b");

    @Override public String strategyId() { return "generic-condition-duration"; }
    @Override public String version() { return "1"; }
    @Override public Set<MechanicType> supportedTypes() { return Set.of(MechanicType.DURATION); }

    @Override
    public StrategyExtraction extract(MechanicCandidate candidate, SourceFragment fragment, SpellDefinition spell) {
        if (fragment.kind() != SourceFragment.Kind.AUTHORED_TEXT) {
            return StrategyExtraction.unsupported("CONDITION_DURATION_RELATION_UNRESOLVED",
                    "The duration cue is not explicitly associated with a recognized condition in authored text.");
        }
        String text = fragment.text();
        Matcher spellEnd = SPELL_END.matcher(text);
        if (spellEnd.find()) {
            Matcher condition = conditionBeforeDuration(text, spellEnd.start());
            if (condition == null) return unresolvedDuration();
            if (spell == null || spell.durations().size() != 1 || fragment.sourceRevision() == null) {
                return StrategyExtraction.unsupported("CONDITION_DURATION_REFERENCE_AMBIGUOUS",
                        "The condition lasts until the spell ends, but a unique pinned spell duration reference is unavailable.");
            }
            var payload = new EffectDurationMechanic(new DeclaredSpellDurationReference(
                    spell.id(), 0, fragment.sourceRevision()));
            return extraction(payload, fragment, spellEnd.start(), spellEnd.end(), condition.start());
        }
        Matcher turnEnd = TURN_END.matcher(text);
        if (turnEnd.find()) {
            Matcher condition = conditionBeforeDuration(text, turnEnd.start());
            if (condition == null) return unresolvedDuration();
            TurnActor actor = turnEnd.group(1).equalsIgnoreCase("your")
                    ? TurnActor.CASTER : TurnActor.AFFECTED_SUBJECT;
            return extraction(new EffectDurationMechanic(new UntilTurnBoundary(actor,
                    TurnBoundary.END_OF_NEXT_TURN)), fragment, turnEnd.start(), turnEnd.end(), condition.start());
        }
        Matcher fixed = FIXED.matcher(text);
        if (fixed.find()) {
            Matcher condition = conditionBeforeDuration(text, fixed.start());
            if (condition == null) return unresolvedDuration();
            DurationUnit unit = DurationUnit.valueOf(fixed.group(3).toUpperCase(Locale.ROOT));
            return extraction(new EffectDurationMechanic(new FixedDuration(unit,
                    Long.parseLong(fixed.group(2)), fixed.group(1) != null)), fragment,
                    fixed.start(), fixed.end(), condition.start());
        }
        return StrategyExtraction.unsupported("CONDITION_DURATION_FORM_UNSUPPORTED",
                "A condition duration was recognized, but its authored boundary is outside the supported typed forms.");
    }

    private static StrategyExtraction extraction(EffectDurationMechanic payload,
            SourceFragment fragment, int start, int end, int subjectIndex) {
        return new StrategyExtraction(List.of(new ExtractedMechanicPayload(payload,
                new MechanicContext(subject(fragment.text(), subjectIndex), List.of()), start, end)), null, null);
    }

    private static Matcher conditionBeforeDuration(String text, int durationStart) {
        Matcher condition = CONDITION.matcher(text);
        Matcher selected = null;
        while (condition.find() && condition.start() < durationStart) {
            selected = CONDITION.matcher(text);
            selected.region(condition.start(), condition.end());
            selected.find();
        }
        if (selected == null) return null;
        String between = text.substring(selected.end(), durationStart);
        if (between.matches("(?s).*?[.!?;].*")) return null;
        return selected;
    }

    private static StrategyExtraction unresolvedDuration() {
        return StrategyExtraction.unsupported("CONDITION_DURATION_RELATION_UNRESOLVED",
                "The duration is not explicitly tied to a condition within the same authored clause.");
    }

    private static SubjectReference subject(String text, int start) {
        String prefix = text.substring(0, Math.min(start, text.length())).toLowerCase(Locale.ROOT);
        String clause = prefix.substring(Math.max(0, prefix.lastIndexOf('.') + 1));
        if (clause.matches("(?s).*\\b(?:each|every) (?:creature|target)\\b.*")) {
            return new SpellTargetSubject(TargetScope.MULTIPLE_TARGET_CREATURES);
        }
        if (clause.matches("(?s).*\\b(?:a|the|one) (?:target|creature|humanoid)\\b.*")) {
            return new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE);
        }
        return new UnknownSubject(UnknownValueReason.NOT_INTERPRETED);
    }
}
