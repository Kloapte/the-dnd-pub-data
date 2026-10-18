package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.dte.common.entry.TextEntryDte;
import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.spell.SpellCastingTime;
import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.mechanics.model.ActivatedBy;
import com.thedndpub.data.mechanics.model.AnyTrigger;
import com.thedndpub.data.mechanics.model.AppliesForDuration;
import com.thedndpub.data.mechanics.model.ArmorClassModifierMechanic;
import com.thedndpub.data.mechanics.model.CasterSubject;
import com.thedndpub.data.mechanics.model.DeclaredSpellDurationReference;
import com.thedndpub.data.mechanics.model.EffectDurationMechanic;
import com.thedndpub.data.mechanics.model.EvidenceRole;
import com.thedndpub.data.mechanics.model.HitByAttackTrigger;
import com.thedndpub.data.mechanics.model.IncludesTriggeringAttack;
import com.thedndpub.data.mechanics.model.IntegerModifier;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.MechanicEvidenceAssociation;
import com.thedndpub.data.mechanics.model.MechanicFamily;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsEvidence;
import com.thedndpub.data.mechanics.model.MechanicsReferenceDisposition;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.model.ReactionActionCost;
import com.thedndpub.data.mechanics.model.ReactionActivationMechanic;
import com.thedndpub.data.mechanics.model.SpellDamagePreventionMechanic;
import com.thedndpub.data.mechanics.model.SpellMentionForm;
import com.thedndpub.data.mechanics.model.SpellMentionReference;
import com.thedndpub.data.mechanics.model.TargetedBySpellTrigger;
import com.thedndpub.data.mechanics.model.TurnActor;
import com.thedndpub.data.mechanics.model.TurnBoundary;
import com.thedndpub.data.mechanics.model.UntilTurnBoundary;
import com.thedndpub.data.mechanics.model.UnsupportedReason;
import com.thedndpub.data.mechanics.syntax.BalancedMarkupParser;
import com.thedndpub.data.mechanics.syntax.MarkupSyntaxException;
import com.thedndpub.data.mechanics.syntax.MarkupTag;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Five deliberately bounded Shield patterns. No general prose interpreter or gameplay execution. */
public final class ShieldMechanicsExtractor implements SpellMechanicsPattern {
    @Override
    public String patternId() {
        return "shield-source-pattern-v1";
    }

    @Override
    public boolean supports(com.thedndpub.data.dto.spell.SpellDefinition spell) {
        return ShieldMechanicsEdition.forId(spell.id()) != null;
    }
    private static final String BODY = "/entries/0";
    private static final String CONDITION = "/time/0/condition";
    private static final String TURN = "Until the start of your next turn";
    private static final String INCLUSION = "including against the triggering attack";
    private static final Pattern TRIGGER = Pattern.compile(
            "^which you take when you are hit by an (?<attack>attack(?: roll)?) or targeted by the (?<spell>.+) spell$");
    private static final Pattern BODY_CLAUSE = Pattern.compile(
            "^Until the start of your next turn, you have a (?<bonus>[+-][0-9]+) bonus to AC, "
                    + "including against the triggering attack, and you take no damage from $");

    public MechanicsDocument extract(SpellDefinition spell, SpellMechanicsEvidenceSource evidence, MechanicsDocument original) {
        ShieldMechanicsEdition edition = ShieldMechanicsEdition.forId(spell.id());
        ModelChecks.require(edition != null, "Unsupported Shield extraction owner");
        ModelChecks.require(original.owner().equals(evidence.owner()) && original.owner().id().equals(spell.id().value())
                        && original.sources().equals(List.of(evidence.source().revision())), "Shield source/document owner mismatch");
        ModelChecks.require(original.processing().state() == ProcessingState.NOT_ATTEMPTED,
                "Shield extraction requires an unattempted input document");
        verifyNormalized(spell, evidence);
        var assembly = new SpellMechanicsAssembly(evidence, original);
        MechanicRecord activation = assembly.attempt("shield-reaction-v1", MechanicFamily.REACTION_ACTIVATION,
                () -> reaction(spell, evidence, assembly, edition));
        assembly.attempt("shield-ac-v1", MechanicFamily.ARMOR_CLASS_MODIFIER,
                () -> armorClass(spell, evidence, assembly, activation));
        assembly.attempt("spell-declared-duration-v1", MechanicFamily.DURATION,
                () -> declaredDuration(spell, evidence, assembly));
        assembly.attempt("shield-turn-boundary-v1", MechanicFamily.DURATION,
                () -> turnBoundary(spell, evidence, assembly));
        assembly.attempt("shield-special-interaction-v1", MechanicFamily.SPELL_DAMAGE_PREVENTION,
                () -> prevention(spell, evidence, assembly, activation));
        return assembly.finish();
    }

    private MechanicRecord reaction(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                                     SpellMechanicsAssembly assembly, ShieldMechanicsEdition edition) {
        require(spell.castingTimes().size() == 1, "/time");
        SpellCastingTime time = spell.castingTimes().getFirst();
        require(Integer.valueOf(1).equals(time.number()) && "reaction".equals(time.unit()) && time.note() == null, "/time/0");
        require(time.condition() != null, CONDITION);
        Matcher match = TRIGGER.matcher(time.condition());
        require(match.matches(), CONDITION);
        String expectedAttack = edition == ShieldMechanicsEdition.PHB ? "attack" : "attack roll";
        require(expectedAttack.equals(match.group("attack")), CONDITION);
        List<MarkupTag> tags = tags(source, CONDITION);
        SpellMentionReference mention;
        if (edition == ShieldMechanicsEdition.PHB) {
            require(tags.size() == 1 && tags.getFirst().originalText().equals(match.group("spell")), CONDITION);
            mention = taggedMention(tags.getFirst(), CONDITION);
        } else {
            require(tags.isEmpty() && match.group("spell").equals("Magic Missile"), CONDITION);
            mention = new SpellMentionReference(match.group("spell"), null, SpellMentionForm.PROSE_MENTION,
                    MechanicsReferenceDisposition.NOT_ATTEMPTED, null, null);
        }
        return assembly.claim("activation", "shield-reaction-v1",
                new ReactionActivationMechanic(new ReactionActionCost(time.number()), new AnyTrigger(List.of(
                        new HitByAttackTrigger(edition.attackEvent()), new TargetedBySpellTrigger(mention)))),
                new MechanicContext(new CasterSubject(), List.of()), List.of(
                        association(source.fieldEvidence("/time/0/number"), "/payload/cost/amount", EvidenceRole.VALUE),
                        association(source.fieldEvidence("/time/0/unit"), "/payload", EvidenceRole.EFFECT_ROLE),
                        association(source.textEvidence(CONDITION, null), "/payload/trigger", EvidenceRole.QUALIFIER),
                        association(source.textEvidence(CONDITION, "you are hit"), "/context/subject", EvidenceRole.QUALIFIER)), false);
    }

    private MechanicRecord armorClass(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                                      SpellMechanicsAssembly assembly, MechanicRecord activation) {
        requireActivation(activation);
        MarkupTag mention = body(spell, source);
        String text = ((TextEntryDte) spell.entries().getFirst()).text();
        int start = text.indexOf(TURN);
        Matcher clause = BODY_CLAUSE.matcher(text.substring(start, mention.evidence().span().startInclusive()));
        require(clause.matches(), BODY);
        String token = clause.group("bonus");
        int value;
        try {
            value = Integer.parseInt(token);
        } catch (NumberFormatException exception) {
            throw new SpellPatternMismatch(UnsupportedReason.UNSUPPORTED_SYNTAX, BODY);
        }
        var activationEvidence = source.textEvidence(CONDITION, null);
        var durationEvidence = source.textEvidence(BODY, TURN);
        var inclusionEvidence = source.textEvidence(BODY, INCLUSION);
        return assembly.claim("ac-bonus", "shield-ac-v1", new ArmorClassModifierMechanic(new IntegerModifier(value, token)),
                new MechanicContext(new CasterSubject(), List.of(
                        new ActivatedBy(activation.revision().reference(activation.id()), List.of(activationEvidence.id())),
                        new AppliesForDuration(turn(), List.of(durationEvidence.id())),
                        new IncludesTriggeringAttack(List.of(inclusionEvidence.id())))), List.of(
                        association(source.textEvidence(BODY, token + " bonus to AC"), "/payload", EvidenceRole.EFFECT_ROLE),
                        association(source.textEvidence(BODY, "you have"), "/context/subject", EvidenceRole.QUALIFIER),
                        association(activationEvidence, "/context/bindings/0", EvidenceRole.QUALIFIER),
                        association(durationEvidence, "/context/bindings/1", EvidenceRole.QUALIFIER),
                        association(inclusionEvidence, "/context/bindings/2", EvidenceRole.QUALIFIER)), false);
    }

    private MechanicRecord declaredDuration(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                                            SpellMechanicsAssembly assembly) {
        requireSelf(spell);
        require(spell.durations().size() == 1, "/duration");
        var duration = spell.durations().getFirst();
        require("timed".equals(duration.type()) && !Boolean.TRUE.equals(duration.concentration())
                && duration.duration() != null && "round".equals(duration.duration().type())
                && Integer.valueOf(1).equals(duration.duration().amount())
                && !Boolean.TRUE.equals(duration.duration().upTo())
                && (duration.ends() == null || duration.ends().isEmpty()), "/duration/0");
        return assembly.claim("structured-duration", "spell-declared-duration-v1",
                new EffectDurationMechanic(new DeclaredSpellDurationReference(spell.id(), 0, source.source().revision())),
                new MechanicContext(new CasterSubject(), List.of()), List.of(
                        association(source.fieldEvidence("/duration/0"), "/payload", EvidenceRole.EFFECT_ROLE),
                        association(source.fieldEvidence("/range/distance/type"), "/context/subject", EvidenceRole.QUALIFIER)), true);
    }

    private MechanicRecord turnBoundary(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                                        SpellMechanicsAssembly assembly) {
        body(spell, source);
        return assembly.claim("turn-boundary", "shield-turn-boundary-v1", new EffectDurationMechanic(turn()),
                new MechanicContext(new CasterSubject(), List.of()), List.of(
                        association(source.textEvidence(BODY, TURN), "/payload", EvidenceRole.EFFECT_ROLE),
                        association(source.textEvidence(BODY, "your next turn"), "/context/subject", EvidenceRole.QUALIFIER)), false);
    }

    private MechanicRecord prevention(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                                      SpellMechanicsAssembly assembly, MechanicRecord activation) {
        requireActivation(activation);
        MarkupTag tag = body(spell, source);
        SpellMentionReference mention = taggedMention(tag, BODY);
        var activationEvidence = source.textEvidence(CONDITION, null);
        var durationEvidence = source.textEvidence(BODY, TURN);
        return assembly.claim("magic-missile-protection", "shield-special-interaction-v1",
                new SpellDamagePreventionMechanic(mention), new MechanicContext(new CasterSubject(), List.of(
                        new ActivatedBy(activation.revision().reference(activation.id()), List.of(activationEvidence.id())),
                        new AppliesForDuration(turn(), List.of(durationEvidence.id())))), List.of(
                        association(source.textEvidence(BODY, "you take no damage from " + tag.originalText()),
                                "/payload", EvidenceRole.EFFECT_ROLE),
                        association(source.textEvidence(BODY, "you take"), "/context/subject", EvidenceRole.QUALIFIER),
                        association(activationEvidence, "/context/bindings/0", EvidenceRole.QUALIFIER),
                        association(durationEvidence, "/context/bindings/1", EvidenceRole.QUALIFIER)), false);
    }

    private MarkupTag body(SpellDefinition spell, SpellMechanicsEvidenceSource source) {
        requireSelf(spell);
        require(spell.entries().size() == 1 && spell.entries().getFirst() instanceof TextEntryDte, "/entries");
        String text = ((TextEntryDte) spell.entries().getFirst()).text();
        int start = text.indexOf(TURN);
        require(start >= 0 && text.indexOf(TURN, start + 1) < 0, BODY);
        List<MarkupTag> tags = tags(source, BODY);
        require(tags.size() == 1, BODY);
        MarkupTag tag = tags.getFirst();
        taggedMention(tag, BODY);
        require(tag.evidence().span().startInclusive() >= start
                && text.substring(tag.evidence().span().endExclusive()).equals("."), BODY);
        require(BODY_CLAUSE.matcher(text.substring(start, tag.evidence().span().startInclusive())).matches(), BODY);
        return tag;
    }

    private SpellMentionReference taggedMention(MarkupTag tag, String pointer) {
        require(tag.name().equals("spell") && tag.children().isEmpty()
                && (tag.arguments().size() == 1 || tag.arguments().size() == 2), pointer);
        String name = tag.arguments().getFirst();
        require(name.equals("magic missile") || name.equals("Magic Missile"), pointer);
        SourceIdentity qualification = null;
        if (tag.arguments().size() == 2) {
            String authoredSource = tag.arguments().get(1);
            require(authoredSource.equals("PHB") || authoredSource.equals("XPHB"), pointer);
            qualification = new SourceIdentity(authoredSource);
        }
        return new SpellMentionReference(name, qualification, SpellMentionForm.TAG_REFERENCE,
                MechanicsReferenceDisposition.NOT_ATTEMPTED, null, null);
    }

    private List<MarkupTag> tags(SpellMechanicsEvidenceSource source, String pointer) {
        try {
            return new BalancedMarkupParser().parse(source.text(pointer)).tags();
        } catch (MarkupSyntaxException exception) {
            throw new SpellPatternMismatch(UnsupportedReason.UNSUPPORTED_SYNTAX, pointer);
        }
    }

    private static void requireSelf(SpellDefinition spell) {
        require(spell.range() != null && "point".equals(spell.range().type()) && spell.range().distance() != null
                && "self".equals(spell.range().distance().type()) && spell.range().distance().amount() == null, "/range");
    }

    private static void requireActivation(MechanicRecord activation) {
        require(activation != null, "/time/0");
    }

    private static UntilTurnBoundary turn() {
        return new UntilTurnBoundary(TurnActor.CASTER, TurnBoundary.START_OF_NEXT_TURN);
    }

    private static MechanicEvidenceAssociation association(MechanicsEvidence evidence, String path, EvidenceRole role) {
        return MechanicEvidenceAssociation.of(evidence, path, role);
    }

    private static void require(boolean matches, String pointer) {
        if (!matches) {
            throw new SpellPatternMismatch(UnsupportedReason.NO_SUPPORTED_PATTERN, pointer);
        }
    }

    private static void verifyNormalized(SpellDefinition spell, SpellMechanicsEvidenceSource source) {
        source.verifyListSize("/time", spell.castingTimes().size());
        for (int index = 0; index < spell.castingTimes().size(); index++) {
            var time = spell.castingTimes().get(index);
            String pointer = "/time/" + index;
            source.verifyInteger(pointer + "/number", time.number());
            source.verifyText(pointer + "/unit", time.unit());
            source.verifyText(pointer + "/condition", time.condition());
            source.verifyText(pointer + "/note", time.note());
        }
        source.verifyListSize("/entries", spell.entries().size());
        for (int index = 0; index < spell.entries().size(); index++) {
            if (spell.entries().get(index) instanceof TextEntryDte text) {
                source.verifyText("/entries/" + index, text.text());
            } else {
                ModelChecks.require(!source.node("/entries/" + index).isTextual(),
                        "Normalized entry shape differs from original source evidence");
            }
        }
        source.verifyPresence("/range", spell.range() != null);
        if (spell.range() != null) {
            source.verifyText("/range/type", spell.range().type());
            source.verifyPresence("/range/distance", spell.range().distance() != null);
            if (spell.range().distance() != null) {
                source.verifyText("/range/distance/type", spell.range().distance().type());
                source.verifyInteger("/range/distance/amount", spell.range().distance().amount());
            }
        }
        source.verifyListSize("/duration", spell.durations().size());
        for (int index = 0; index < spell.durations().size(); index++) {
            var duration = spell.durations().get(index);
            String pointer = "/duration/" + index;
            source.verifyText(pointer + "/type", duration.type());
            source.verifyBoolean(pointer + "/concentration", duration.concentration());
            source.verifyTextList(pointer + "/ends", duration.ends());
            source.verifyPresence(pointer + "/duration", duration.duration() != null);
            if (duration.duration() != null) {
                source.verifyText(pointer + "/duration/type", duration.duration().type());
                source.verifyInteger(pointer + "/duration/amount", duration.duration().amount());
                source.verifyBoolean(pointer + "/duration/upTo", duration.duration().upTo());
            }
        }
    }
}
