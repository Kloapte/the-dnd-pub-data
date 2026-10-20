package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.dte.common.entry.TextEntryDte;
import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.mechanics.model.DamageInstanceSeriesMechanic;
import com.thedndpub.data.mechanics.model.DamageRollGrouping;
import com.thedndpub.data.mechanics.model.DamageResolutionMode;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.EffectTiming;
import com.thedndpub.data.mechanics.model.EvidenceRole;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.InstanceTargetAllocation;
import com.thedndpub.data.mechanics.model.InstanceTargetRequirement;
import com.thedndpub.data.mechanics.model.IntegerAmount;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.MechanicEvidenceAssociation;
import com.thedndpub.data.mechanics.model.MechanicFamily;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.MechanicsCompleteness;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.model.SpellSlotInstanceCount;
import com.thedndpub.data.mechanics.model.SpellTargetSubject;
import com.thedndpub.data.mechanics.model.SumAmount;
import com.thedndpub.data.mechanics.model.TargetCategory;
import com.thedndpub.data.mechanics.model.TargetReusePolicy;
import com.thedndpub.data.mechanics.model.TargetScope;
import com.thedndpub.data.mechanics.model.TargetSelectionCardinality;
import com.thedndpub.data.mechanics.model.UnsupportedReason;
import com.thedndpub.data.mechanics.syntax.MarkupSyntaxException;
import com.thedndpub.data.mechanics.syntax.MarkupTag;
import com.thedndpub.data.mechanics.syntax.BalancedMarkupParser;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Bounded source rule for the single PHB automatic-hit damage-instance series. */
final class PhbMagicMissileRules {
    static final String OWNER = "spell:phb:magic-missile";
    private static final String PINNED_SOURCE_SHA256 =
            "c9e54cce55e7ce0fc988feb1b604bf505faa5c042635542fa6b6ed36724e297a";
    private static final String BODY = "/entries/0";
    private static final String HIGHER = "/entriesHigherLevel/0/entries/0";
    private static final String BODY_TEXT = "You create three glowing darts of magical force. Each dart hits a "
            + "creature of your choice that you can see within range. A dart deals {@damage 1d4 + 1} force damage "
            + "to its target. The darts all strike simultaneously, and you can direct them to hit one creature or "
            + "several.";
    private static final String HIGHER_TEXT = "When you cast this spell using a spell slot of 2nd level or higher, "
            + "the spell creates one more dart for each slot level above 1st.";
    private static final Pattern FORMULA = Pattern.compile("([1-9][0-9]*)d([1-9][0-9]*)\\s*\\+\\s*([0-9]+)");

    private PhbMagicMissileRules() {
    }

    static MechanicsDocument extract(
            SpellDefinition spell,
            SpellMechanicsEvidenceSource source,
            MechanicsDocument original) {
        ModelChecks.require(spell.id().value().equals(OWNER)
                        && original.owner().equals(source.owner())
                        && original.owner().id().equals(OWNER)
                        && source.owner().source().identity().code().equals("PHB")
                        && source.source().jsonPointer().equals("/spell/213")
                        && source.source().revision().resourcePath().equals("data/spells/spells-phb.json")
                        && source.source().revision().resourceSha256().equals(PINNED_SOURCE_SHA256)
                        && original.sources().equals(List.of(source.source().revision())),
                "Magic Missile extraction owner/source mismatch");
        ModelChecks.require(original.processing().state() == ProcessingState.NOT_ATTEMPTED,
                "Magic Missile extraction requires an unattempted document");
        ModelChecks.require(spell.level() == 1, "Magic Missile must be a 1st-level spell");
        ModelChecks.require(spell.entries().size() == 1 && spell.entries().getFirst() instanceof TextEntryDte,
                "Magic Missile body shape changed");
        ModelChecks.require(spell.entriesHigherLevel().size() == 1, HIGHER);
        ModelChecks.require(spell.mechanics().spellAttacks().isEmpty(), "/spellAttack");
        ModelChecks.require(spell.mechanics().damageInflicted().equals(List.of("force")), "/damageInflict");

        requireText(source, BODY, BODY_TEXT);
        requireText(source, HIGHER, HIGHER_TEXT);
        MarkupTag formula = formula(source);
        Matcher match = FORMULA.matcher(formula.arguments().getFirst());
        ModelChecks.require(match.matches(), "Magic Missile damage formula changed");

        SpellMechanicsAssembly assembly = new SpellMechanicsAssembly(source, original,
                "phb-magic-missile-automatic-hit-v1");
        assembly.attempt("damage-per-dart-v1", MechanicFamily.DAMAGE_INSTANCE_SERIES,
                () -> damageSeries(source, assembly, formula, match));
        String root = source.source().jsonPointer();
        return assembly.finish(List.of(root + "/entries", root + "/entriesHigherLevel",
                        root + "/damageInflict", root + "/spellAttack", root + "/range"),
                List.of(root + "/entries"));
    }

    private static MechanicRecord damageSeries(
            SpellMechanicsEvidenceSource source,
            SpellMechanicsAssembly assembly,
            MarkupTag formula,
            Matcher match) {
        int diceCount = positiveInt(match.group(1));
        int diceSides = positiveInt(match.group(2));
        int modifier = nonNegativeInt(match.group(3));
        var amount = new SumAmount(List.of(new DiceAmount(diceCount, diceSides, 0), new IntegerAmount(modifier)));
        var allocation = new InstanceTargetAllocation(
                TargetSelectionCardinality.EXACTLY_ONE_PER_INSTANCE,
                List.of(TargetCategory.CREATURE),
                TargetReusePolicy.REUSE_ALLOWED,
                true,
                null,
                List.of(InstanceTargetRequirement.VISIBLE_TO_CASTER));
        var payload = new DamageInstanceSeriesMechanic(amount, new FixedDamageType("force"),
                new SpellSlotInstanceCount(1, 3, 1), allocation, DamageResolutionMode.AUTOMATIC_HIT,
                EffectTiming.SIMULTANEOUS, DamageRollGrouping.UNSPECIFIED);

        var countEvidence = source.textEvidence(BODY, "three glowing darts");
        var targetingEvidence = source.textEvidence(BODY,
                "Each dart hits a creature of your choice that you can see within range.");
        var rangeEvidence = source.fieldEvidence("/range");
        var damageEvidence = source.textEvidence(BODY, formula.originalText());
        var typeEvidence = source.fieldEvidence("/damageInflict/0");
        var timingEvidence = source.textEvidence(BODY, "The darts all strike simultaneously");
        var allocationEvidence = source.textEvidence(BODY,
                "you can direct them to hit one creature or several");
        var scalingEvidence = source.textEvidence("/entriesHigherLevel/0/entries/0",
                "one more dart for each slot level above 1st");
        return assembly.claim("damage-per-dart", "magic-missile-automatic-hit-series-v1", payload,
                new MechanicContext(new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE), List.of()),
                List.of(
                        MechanicEvidenceAssociation.of(damageEvidence, "/payload",
                                EvidenceRole.EFFECT_ROLE),
                        MechanicEvidenceAssociation.of(typeEvidence, "/payload/damageType", EvidenceRole.REFERENCE),
                        MechanicEvidenceAssociation.of(countEvidence, "/payload/countProgression",
                                EvidenceRole.SCALING),
                        MechanicEvidenceAssociation.of(scalingEvidence, "/payload/countProgression",
                                EvidenceRole.SCALING),
                        MechanicEvidenceAssociation.of(targetingEvidence, "/payload/targetAllocation",
                                EvidenceRole.SUBJECT),
                        MechanicEvidenceAssociation.of(rangeEvidence, "/payload/targetAllocation",
                                EvidenceRole.QUALIFIER),
                        MechanicEvidenceAssociation.of(allocationEvidence, "/payload/targetAllocation",
                                EvidenceRole.QUALIFIER),
                        MechanicEvidenceAssociation.of(timingEvidence, "/payload/timing", EvidenceRole.VALUE)),
                false, MechanicsCompleteness.PARTIAL);
    }

    private static MarkupTag formula(SpellMechanicsEvidenceSource source) {
        try {
            List<MarkupTag> tags = new BalancedMarkupParser().parse(source.text(BODY)).tags();
            ModelChecks.require(tags.size() == 1, BODY);
            MarkupTag tag = tags.getFirst();
            ModelChecks.require(tag.name().equals("damage") && tag.arguments().size() == 1
                            && tag.children().isEmpty(), BODY);
            return tag;
        } catch (MarkupSyntaxException exception) {
            throw new SpellPatternMismatch(UnsupportedReason.UNSUPPORTED_SYNTAX, BODY);
        }
    }

    private static void requireText(SpellMechanicsEvidenceSource source, String pointer, String expected) {
        ModelChecks.require(source.text(pointer).text().equals(expected), pointer + " source text changed");
    }

    private static int positiveInt(String value) {
        try {
            int parsed = Integer.parseInt(value);
            ModelChecks.require(parsed > 0, "Damage dice values must be positive");
            return parsed;
        } catch (NumberFormatException exception) {
            throw new SpellPatternMismatch(UnsupportedReason.UNSUPPORTED_SYNTAX, BODY);
        }
    }

    private static int nonNegativeInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new SpellPatternMismatch(UnsupportedReason.UNSUPPORTED_SYNTAX, BODY);
        }
    }
}
