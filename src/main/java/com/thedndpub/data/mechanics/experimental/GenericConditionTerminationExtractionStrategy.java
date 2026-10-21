package com.thedndpub.data.mechanics.experimental;

import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.mechanics.model.Ability;
import com.thedndpub.data.mechanics.model.ConditionTerminationActor;
import com.thedndpub.data.mechanics.model.ConditionTerminationEventMechanic;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.UnknownSubject;
import com.thedndpub.data.mechanics.model.UnknownValueReason;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Extracts explicit action-plus-success condition termination opportunities. */
public final class GenericConditionTerminationExtractionStrategy implements MechanicExtractionStrategy {
    private static final Pattern EVENT = Pattern.compile("(?is)\\b(?:can use|uses) (?:its|their|the target's) action\\b"
            + "[^;]{0,180}\\b(?:check|attempt|escape|free itself|free the target)\\b"
            + "[^;]{0,180}\\b(?:on a success|if it succeeds|if successful|on success)\\b"
            + "[^.;]{0,100}(?:\\b(?:free|frees|escape|escapes|end|ends|remove|removes)\\b|\\bis no longer\\b)");
    private static final Pattern ABILITY = Pattern.compile("(?i)\\b(strength|dexterity|constitution|intelligence|"
            + "wisdom|charisma) check\\b");
    private static final Pattern ELIGIBLE_HELPER = Pattern.compile(
            "(?i)\\b(?:or|and) (?:one|a creature) that can touch (?:the )?(?:target|creature)\\b");

    @Override public String strategyId() { return "generic-condition-termination-event"; }
    @Override public String version() { return "1"; }
    @Override public Set<MechanicType> supportedTypes() { return Set.of(MechanicType.CONDITION_TERMINATION); }

    @Override
    public StrategyExtraction extract(MechanicCandidate candidate, SourceFragment fragment, SpellDefinition spell) {
        if (fragment.kind() != SourceFragment.Kind.AUTHORED_TEXT) {
            return StrategyExtraction.unsupported("CONDITION_TERMINATION_EVENT_UNRESOLVED",
                    "A condition-ending event requires explicit authored action and success wording.");
        }
        Matcher event = EVENT.matcher(fragment.text());
        if (!event.find()) {
            return StrategyExtraction.unsupported("CONDITION_TERMINATION_EVENT_UNRESOLVED",
                    "The source does not establish an action that successfully ends an existing condition.");
        }
        String wording = event.group();
        Matcher abilityMatcher = ABILITY.matcher(wording);
        Ability ability = abilityMatcher.find()
                ? Ability.valueOf(abilityMatcher.group(1).toUpperCase(java.util.Locale.ROOT)) : null;
        ConditionTerminationActor actor = ELIGIBLE_HELPER.matcher(fragment.text()).find()
                ? ConditionTerminationActor.AFFECTED_SUBJECT_OR_ELIGIBLE_OTHER_CREATURE
                : ConditionTerminationActor.AFFECTED_SUBJECT;
        return new StrategyExtraction(List.of(new ExtractedMechanicPayload(
                new ConditionTerminationEventMechanic(actor, ability),
                new MechanicContext(new UnknownSubject(UnknownValueReason.NOT_INTERPRETED), List.of()),
                event.start(), event.end())), null, null);
    }
}
