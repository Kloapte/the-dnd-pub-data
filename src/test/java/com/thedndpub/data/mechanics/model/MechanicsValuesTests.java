package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.spell.SpellId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.thedndpub.data.mechanics.model.MechanicsModelFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

class MechanicsValuesTests {
    private final ObjectMapper mapper = MechanicsJson.mapper();

    static Stream<MechanicPayload> payloads() {
        return Stream.of(
                new ArmorClassModifierMechanic(new IntegerModifier(5, "+5")),
                new ReactionActivationMechanic(new ReactionActionCost(1), new AnyTrigger(List.of(
                        new HitByAttackTrigger(AttackEventKind.ATTACK_HIT),
                        new TargetedBySpellTrigger(unresolvedSpell())))),
                new EffectDurationMechanic(new UntilTurnBoundary(TurnActor.CASTER, TurnBoundary.START_OF_NEXT_TURN)),
                new SpellDamagePreventionMechanic(unresolvedSpell()),
                new DamageMechanic(new DiceAmount(8, 6, 0), new FixedDamageType("fire")),
                new HealingMechanic(new SumAmount(List.of(new DiceAmount(1, 4, 0),
                        new SymbolicAmount(AmountSymbol.CASTER_SPELLCASTING_ABILITY_MODIFIER))), HealingMode.RESTORE_HIT_POINTS),
                new SavingThrowRequirementMechanic(new FixedAbility(Ability.DEXTERITY),
                        new UnknownSaveDifficulty(UnknownValueReason.NOT_AUTHORED)),
                new ConditionChangeMechanic(ConditionOperation.APPLY, ConditionName.CHARMED,
                        List.of("on a failed Wisdom saving throw")),
                new ConditionChangeMechanic(ConditionOperation.REMOVE, ConditionName.POISONED, List.of()),
                new ConditionChangeMechanic(ConditionOperation.GRANT_IMMUNITY, ConditionName.FRIGHTENED, List.of()),
                new ConditionChoiceMechanic(ConditionOperation.APPLY,
                        List.of(ConditionName.BLINDED, ConditionName.DEAFENED), 1));
    }

    static SpellMentionReference unresolvedSpell() {
        return new SpellMentionReference("magic missile", null, SpellMentionForm.TAG_REFERENCE,
                MechanicsReferenceDisposition.NOT_ATTEMPTED, null, null);
    }

    @ParameterizedTest
    @MethodSource("payloads")
    void eachInitialPayloadHasALosslessNamedPolymorphicRoundTrip(MechanicPayload payload) throws Exception {
        String json = mapper.writerFor(MechanicPayload.class).writeValueAsString(payload);
        assertTrue(mapper.readTree(json).has("kind"));
        assertFalse(json.contains("@class"));
        assertEquals(payload, mapper.readValue(json, MechanicPayload.class));
        assertEquals(json, mapper.writerFor(MechanicPayload.class).writeValueAsString(payload));
    }

    static Stream<AmountExpression> amounts() {
        return Stream.of(new IntegerAmount(0), new DiceAmount(1, 4, 0),
                new SymbolicAmount(AmountSymbol.CASTER_SPELLCASTING_ABILITY_MODIFIER),
                new SumAmount(List.of(new IntegerAmount(0), new DiceAmount(1, 4, -1))),
                new UnknownAmount(UnknownValueReason.NOT_INTERPRETED));
    }

    @ParameterizedTest
    @MethodSource("amounts")
    void amountsAreTypedValuesRatherThanEvaluatedResults(AmountExpression value) throws Exception {
        assertEquals(value, mapper.readValue(mapper.writerFor(AmountExpression.class).writeValueAsBytes(value), AmountExpression.class));
    }

    @Test
    void preservesSignedTokensAndRejectsUnsupportedOrInconsistentFormats() throws Exception {
        for (var value : List.of(new IntegerModifier(5, "+5"), new IntegerModifier(-2, "-2"),
                new IntegerModifier(0, "+0"), new IntegerModifier(0, "-0"), new IntegerModifier(0, null))) {
            assertEquals(value, mapper.readValue(mapper.writerFor(ModifierAmount.class).writeValueAsBytes(value), ModifierAmount.class));
        }
        assertEquals(new SymbolicModifier(AmountSymbol.CHARACTER_LEVEL), mapper.readValue(
                mapper.writerFor(ModifierAmount.class).writeValueAsBytes(new SymbolicModifier(AmountSymbol.CHARACTER_LEVEL)),
                ModifierAmount.class));
        for (String invalid : List.of("1d4", "+ 1", "{@hit 5}", "one", "2147483648")) {
            assertThrows(IllegalArgumentException.class, () -> new IntegerModifier(1, invalid));
        }
        assertThrows(IllegalArgumentException.class, () -> new IntegerModifier(5, "+6"));
    }

    @Test
    void preservesZeroFalseAndTheDifferenceBetweenAbsentAndUnknown() throws Exception {
        var explicitFalse = new FixedDuration(DurationUnit.ROUND, 0, false);
        var absent = new FixedDuration(DurationUnit.ROUND, 0, null);
        var json = mapper.readTree(mapper.writerFor(DurationSpecification.class).writeValueAsBytes(explicitFalse));
        assertTrue(json.has("amount"));
        assertEquals(0, json.path("amount").asInt());
        assertTrue(json.has("upTo"));
        assertFalse(json.path("upTo").asBoolean());
        assertFalse(mapper.readTree(mapper.writeValueAsBytes(absent)).has("upTo"));
        assertNotEquals(explicitFalse, absent);
        assertEquals(explicitFalse, mapper.readValue(mapper.writerFor(DurationSpecification.class)
                .writeValueAsBytes(explicitFalse), DurationSpecification.class));
        assertEquals(new UnknownDuration(UnknownValueReason.NOT_INTERPRETED), mapper.readValue(
                "{\"kind\":\"UNKNOWN\",\"reason\":\"NOT_INTERPRETED\"}", DurationSpecification.class));
        assertThrows(Exception.class, () -> mapper.readValue("{\"kind\":\"DICE\",\"count\":1,\"sides\":4}", AmountExpression.class));
        assertThrows(Exception.class, () -> mapper.readValue("{\"kind\":\"INTEGER\",\"value\":null}", AmountExpression.class));
        assertThrows(Exception.class, () -> mapper.readValue("{\"kind\":\"INTEGER\",\"value\":0.5}", AmountExpression.class));
    }

    @Test
    void sourceAwareIdentityUsesStableSlotsNotValuesOrProjectionOrder() {
        var phb = MechanicId.of(PHB, "ac-bonus");
        var xphb = MechanicId.of(XPHB, "ac-bonus");
        assertEquals("mechanic:spell:phb:shield:ac-bonus", phb.value());
        assertNotEquals(phb, xphb);
        assertEquals(PHB.id(), phb.ownerId());
        assertEquals(phb, revise(ac(), new ArmorClassModifierMechanic(new IntegerModifier(6, "+6"))).id());
        assertThrows(IllegalArgumentException.class, () -> MechanicId.of(PHB, "filtered:0"));
        assertThrows(IllegalArgumentException.class,
                () -> new MechanicsOwnerReference(CatalogDomain.SPELL, PHB.id(), XPHB.source()));
        assertThrows(IllegalArgumentException.class,
                () -> new MechanicsOwnerReference(CatalogDomain.ITEM, PHB.id(), PHB.source()));
    }

    @Test
    void spellQualificationAndAttackEventWordingRemainIndependent() throws Exception {
        var phb = unresolvedSpell();
        var xphb = new SpellMentionReference("Magic Missile", new SourceIdentity("XPHB"),
                SpellMentionForm.TAG_REFERENCE, MechanicsReferenceDisposition.NOT_ATTEMPTED, null, null);
        assertNull(phb.authoredSource());
        assertEquals("XPHB", xphb.authoredSource().code());
        assertEquals(xphb, mapper.readValue(mapper.writeValueAsBytes(xphb), SpellMentionReference.class));
        assertNotEquals(new HitByAttackTrigger(AttackEventKind.ATTACK_HIT),
                new HitByAttackTrigger(AttackEventKind.ATTACK_ROLL_HIT));
        assertThrows(IllegalArgumentException.class, () -> new SpellMentionReference("Magic Missile", null,
                SpellMentionForm.PROSE_MENTION, MechanicsReferenceDisposition.RESOLVED,
                new SpellId("spell:phb:magic-missile"), HASH));
        assertThrows(IllegalArgumentException.class, () -> new SpellMentionReference("Magic Missile", new SourceIdentity("XPHB"),
                SpellMentionForm.TAG_REFERENCE, MechanicsReferenceDisposition.RESOLVED,
                new SpellId("spell:phb:magic-missile"), HASH));
        assertThrows(IllegalArgumentException.class, () -> new SpellMentionReference("Magic Missile", null,
                SpellMentionForm.TAG_REFERENCE, MechanicsReferenceDisposition.NOT_ATTEMPTED,
                new SpellId("spell:phb:magic-missile"), HASH));
    }

    @Test
    void orderedTriggerGroupsAndChoicesAreDefensivelyCopiedWithoutSelectingAnAlternative() throws Exception {
        var triggers = new ArrayList<TriggerExpression>(List.of(new HitByAttackTrigger(AttackEventKind.ATTACK_HIT),
                new TargetedBySpellTrigger(unresolvedSpell())));
        var any = new AnyTrigger(triggers);
        var all = new AllTriggers(List.of(any, new HitByAttackTrigger(AttackEventKind.ATTACK_ROLL_HIT)));
        triggers.clear();
        assertEquals(2, any.alternatives().size());
        assertThrows(UnsupportedOperationException.class, () -> any.alternatives().clear());
        assertEquals(all, mapper.readValue(mapper.writerFor(TriggerExpression.class).writeValueAsBytes(all), TriggerExpression.class));
        var choices = new ArrayList<>(List.of("cold", "fire"));
        var selection = new DamageTypeChoice(choices, null);
        choices.clear();
        assertEquals(List.of("cold", "fire"), selection.alternatives());
        assertNull(selection.count());
        assertEquals(selection, mapper.readValue(mapper.writerFor(DamageTypeSelection.class)
                .writeValueAsBytes(selection), DamageTypeSelection.class));
        assertThrows(IllegalArgumentException.class, () -> new DamageTypeChoice(List.of("fire"), 0));
        assertThrows(IllegalArgumentException.class, () -> new AnyTrigger(List.of()));
    }

    @Test
    void remainingSelectorsSubjectsAndDurationsRoundTrip() throws Exception {
        for (var value : List.of(new FixedAbility(Ability.DEXTERITY), new AbilityChoice(List.of(Ability.STRENGTH, Ability.DEXTERITY), 1),
                new UnknownAbility(UnknownValueReason.NOT_INTERPRETED))) {
            assertEquals(value, mapper.readValue(mapper.writerFor(AbilitySelection.class).writeValueAsBytes(value), AbilitySelection.class));
        }
        for (var value : List.of(new FixedDamageType("fire"), new UnknownDamageType(UnknownValueReason.AMBIGUOUS))) {
            assertEquals(value, mapper.readValue(mapper.writerFor(DamageTypeSelection.class).writeValueAsBytes(value), DamageTypeSelection.class));
        }
        for (var value : List.of(new CasterSubject(), new UnknownSubject(UnknownValueReason.NOT_INTERPRETED))) {
            assertEquals(value, mapper.readValue(mapper.writerFor(SubjectReference.class).writeValueAsBytes(value), SubjectReference.class));
        }
        for (var value : List.of(new FixedSaveDifficulty(0), new UnknownSaveDifficulty(UnknownValueReason.NOT_AUTHORED))) {
            assertEquals(value, mapper.readValue(mapper.writerFor(SaveDifficulty.class).writeValueAsBytes(value), SaveDifficulty.class));
        }
        var declared = new DeclaredSpellDurationReference(new SpellId(PHB.id()), 0, artifact("PHB"));
        var referenced = new ReferencedDuration(ac().revision().reference(ac().id()));
        for (var value : List.of(declared, referenced)) {
            byte[] json = mapper.writerFor(DurationSpecification.class).writeValueAsBytes(value);
            assertEquals(value, mapper.readValue(json, DurationSpecification.class));
        }
    }

    static Stream<Class<?>> polymorphicRoots() {
        return Stream.of(MechanicPayload.class, AmountExpression.class, ModifierAmount.class, TriggerExpression.class,
                DurationSpecification.class, AbilitySelection.class, DamageTypeSelection.class, SaveDifficulty.class,
                SubjectReference.class, MechanicContextBinding.class, MechanicsEvidence.class, MechanicsObservationReference.class);
    }

    @ParameterizedTest
    @MethodSource("polymorphicRoots")
    void unknownAndMissingDiscriminatorsFailWithoutJavaClassNameFallback(Class<?> type) {
        assertThrows(Exception.class, () -> mapper.readValue("{\"kind\":\"UNREVIEWED_FUTURE_KIND\"}", type));
        assertThrows(Exception.class, () -> mapper.readValue("{\"@class\":\"java.lang.String\"}", type));
    }

    @Test
    void invalidNumericAndRevisionValuesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new DiceAmount(0, 6, 0));
        assertThrows(IllegalArgumentException.class, () -> new DiceAmount(1, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new IntegerAmount(-1));
        assertThrows(IllegalArgumentException.class, () -> new SumAmount(List.of(new IntegerAmount(1))));
        assertThrows(IllegalArgumentException.class, () -> new ReactionActionCost(0));
        assertThrows(IllegalArgumentException.class, () -> new MechanicRevision(2, HASH, null));
        assertThrows(IllegalArgumentException.class, () -> new MechanicRevision(1, HASH, ac().revision().reference(ac().id())));
        assertThrows(IllegalArgumentException.class, () -> new SourceArtifactRevision("v1", "../data/test.json", HASH));
    }
}
