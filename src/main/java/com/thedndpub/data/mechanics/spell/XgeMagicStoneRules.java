package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.mechanics.model.AppliesOnAttackRollOutcome;
import com.thedndpub.data.mechanics.model.AmountSymbol;
import com.thedndpub.data.mechanics.model.AttackActivationTiming;
import com.thedndpub.data.mechanics.model.AttackActorRole;
import com.thedndpub.data.mechanics.model.AttackKind;
import com.thedndpub.data.mechanics.model.AttackModifierAttribution;
import com.thedndpub.data.mechanics.model.AttackRollMechanic;
import com.thedndpub.data.mechanics.model.AttackRollOutcome;
import com.thedndpub.data.mechanics.model.CoverageStatus;
import com.thedndpub.data.mechanics.model.CoverageScopeExtent;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.DeclaredSpellDurationReference;
import com.thedndpub.data.mechanics.model.DerivationMethod;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.EnchantedObjectAttackUse;
import com.thedndpub.data.mechanics.model.EnchantedObjectCategory;
import com.thedndpub.data.mechanics.model.EnchantedObjectCollectionMechanic;
import com.thedndpub.data.mechanics.model.EnchantedObjectDelivery;
import com.thedndpub.data.mechanics.model.EnchantedObjectDeliveryMode;
import com.thedndpub.data.mechanics.model.EnchantedObjectUseConsumption;
import com.thedndpub.data.mechanics.model.EnchantedObjectUserRole;
import com.thedndpub.data.mechanics.model.EvidenceRole;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.MechanicEvidenceAssociation;
import com.thedndpub.data.mechanics.model.MechanicFamily;
import com.thedndpub.data.mechanics.model.MechanicId;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.MechanicRevisionReference;
import com.thedndpub.data.mechanics.model.MechanicsAssessment;
import com.thedndpub.data.mechanics.model.MechanicsCompleteness;
import com.thedndpub.data.mechanics.model.MechanicsCoverage;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsProcessing;
import com.thedndpub.data.mechanics.model.MechanicsSupport;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.model.SpellTargetSubject;
import com.thedndpub.data.mechanics.model.SpellCastingRangeKind;
import com.thedndpub.data.mechanics.model.SumAmount;
import com.thedndpub.data.mechanics.model.SymbolicAmount;
import com.thedndpub.data.mechanics.model.TargetScope;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.mechanics.model.UnsupportedMechanicsConstruct;
import com.thedndpub.data.mechanics.model.UnsupportedReason;

import java.util.List;

/** Source-bounded XGE Magic Stone interpretation. */
final class XgeMagicStoneRules implements SpellMechanicsPattern {
    static final String OWNER_ID = "spell:xge:magic-stone";
    private static final String BODY = "/entries/0";
    private static final String RECAST = "/entries/1";
    private static final String ENCHANT = "You touch one to three pebbles and imbue them with magic.";
    private static final String USER = "You or someone else can make a ranged spell attack with one of the pebbles";
    private static final String DELIVERY = "by throwing it or hurling it with a {@item sling|phb}.";
    private static final String THROWN_RANGE = "If thrown, a pebble has a range of 60 feet.";
    private static final String OTHER_ACTOR = "If someone else attacks with a pebble, that attacker adds your "
            + "spellcasting ability modifier, not the attacker's, to the attack roll.";
    private static final String HIT = "On a hit";
    private static final String DAMAGE = "the target takes bludgeoning damage equal to {@damage 1d6} + your "
            + "spellcasting ability modifier.";
    private static final String CONSUMPTION = "Whether the attack hits or misses, the spell then ends on the stone.";
    private static final String RECAST_ENDS = "If you cast this spell again, the spell ends on any pebbles still "
            + "affected by your previous casting.";

    @Override
    public String patternId() {
        return "xge-magic-stone-transferable-consumable-object-v1";
    }

    @Override
    public boolean supports(SpellDefinition spell) {
        return OWNER_ID.equals(spell.id().value()) && "XGE".equals(spell.source().identity().code());
    }

    @Override
    public MechanicsDocument extract(
            SpellDefinition spell, SpellMechanicsEvidenceSource source, MechanicsDocument original) {
        if (!supports(spell) || original.processing().state() != ProcessingState.NOT_ATTEMPTED) {
            throw new IllegalArgumentException("Only the pinned XGE Magic Stone owner is supported");
        }
        TextMechanicsEvidence enchant = source.textEvidence(BODY, ENCHANT);
        TextMechanicsEvidence use = source.textEvidence(BODY, USER);
        TextMechanicsEvidence deliveryText = source.textEvidence(BODY, DELIVERY);
        TextMechanicsEvidence thrownRangeText = source.textEvidence(BODY, THROWN_RANGE);
        TextMechanicsEvidence otherActorText = source.textEvidence(BODY, OTHER_ACTOR);
        TextMechanicsEvidence hit = source.textEvidence(BODY, HIT);
        TextMechanicsEvidence damageText = source.textEvidence(BODY, DAMAGE);
        TextMechanicsEvidence consumption = source.textEvidence(BODY, CONSUMPTION);
        TextMechanicsEvidence recast = source.textEvidence(RECAST, RECAST_ENDS);
        TextMechanicsEvidence formula = source.textEvidence(BODY, "{@damage 1d6}");
        var time = source.fieldEvidence("/time/0");
        var range = source.fieldEvidence("/range");
        var duration = source.fieldEvidence("/duration/0");
        var attackKind = source.fieldEvidence("/spellAttack/0");
        var damageType = source.fieldEvidence("/damageInflict/0");

        var allEvidence = source.evidence();
        String root = source.source().jsonPointer();
        MechanicId objectId = MechanicId.of(source.owner(), "enchanted-stones");
        MechanicRecord objects = claim(source, objectId, "enchanted-stones", "magic-stone-objects-v1",
                new EnchantedObjectCollectionMechanic(EnchantedObjectCategory.PEBBLE, 3, false,
                        AttackActivationTiming.BONUS_ACTION, SpellCastingRangeKind.TOUCH,
                        new DeclaredSpellDurationReference(spell.id(), 0, source.source().revision()),
                        EnchantedObjectUseConsumption.ON_ATTACK_USE_REGARDLESS_OF_OUTCOME, true, true),
                List.of(association(enchant, "/payload", EvidenceRole.EFFECT_ROLE),
                        association(enchant, "/payload/maximumObjectCount", EvidenceRole.VALUE),
                        association(enchant, "/payload/objectsCreatedBySpell", EvidenceRole.QUALIFIER),
                        association(time, "/payload/enchantmentActivationTiming", EvidenceRole.TRIGGER),
                        association(range, "/payload/spellCastingRange", EvidenceRole.QUALIFIER),
                        association(consumption, "/payload/useConsumption", EvidenceRole.TRIGGER),
                        association(duration, "/payload/enchantmentDuration", EvidenceRole.DURATION),
                        association(recast, "/payload/recastingEndsUnusedEnchantments", EvidenceRole.QUALIFIER)),
                allEvidence,
                new MechanicContext(new SpellTargetSubject(TargetScope.SINGLE_TARGET_UNSPECIFIED_CATEGORY), List.of()),
                MechanicsCompleteness.COMPLETE_WITHIN_CLAIM);

        MechanicRevisionReference objectReference = objects.revision().reference(objects.id());
        var attackUse = new EnchantedObjectAttackUse(objectReference,
                List.of(EnchantedObjectUserRole.SPELL_CASTER, EnchantedObjectUserRole.OTHER_CREATURE),
                AttackModifierAttribution.SPELL_CASTER_SPELLCASTING_ABILITY,
                new EnchantedObjectDelivery(List.of(EnchantedObjectDeliveryMode.THROW,
                        EnchantedObjectDeliveryMode.SLING), 60));
        MechanicRecord attack = claim(source, MechanicId.of(source.owner(), "attack-roll"), "attack-roll",
                "magic-stone-attack-v1",
                new AttackRollMechanic(AttackKind.RANGED_SPELL, AttackActorRole.ENCHANTED_OBJECT_USER,
                        1, false, AttackActivationTiming.OBJECT_USE, attackUse),
                List.of(association(use, "/payload", EvidenceRole.EFFECT_ROLE),
                        association(use, "/payload/attackCount", EvidenceRole.VALUE),
                        association(use, "/payload/activationTiming", EvidenceRole.TRIGGER),
                        association(deliveryText, "/payload/enchantedObjectUse/delivery/modes", EvidenceRole.QUALIFIER),
                        association(thrownRangeText,
                                "/payload/enchantedObjectUse/delivery/thrownRangeFeet", EvidenceRole.VALUE),
                        association(otherActorText,
                                "/payload/enchantedObjectUse/attackModifierAttribution", EvidenceRole.SUBJECT),
                        association(attackKind, "/payload/attackKind", EvidenceRole.VALUE),
                        association(use, "/payload/enchantedObjectUse", EvidenceRole.QUALIFIER)),
                allEvidence,
                new MechanicContext(new SpellTargetSubject(TargetScope.SINGLE_TARGET_UNSPECIFIED_CATEGORY), List.of()),
                MechanicsCompleteness.PARTIAL);

        MechanicRevisionReference attackReference = attack.revision().reference(attack.id());
        MechanicRecord damageRecord = claim(source, MechanicId.of(source.owner(), "damage"), "damage",
                "magic-stone-hit-damage-v1",
                new DamageMechanic(new SumAmount(List.of(new DiceAmount(1, 6, 0),
                        new SymbolicAmount(AmountSymbol.CASTER_SPELLCASTING_ABILITY_MODIFIER))),
                        new FixedDamageType("bludgeoning")),
                List.of(association(damageText, "/payload", EvidenceRole.EFFECT_ROLE),
                        association(formula, "/payload/amount", EvidenceRole.VALUE),
                        association(otherActorText, "/payload/amount", EvidenceRole.SUBJECT),
                        association(damageType, "/payload/damageType", EvidenceRole.VALUE),
                        association(hit, "/context/bindings/0", EvidenceRole.TRIGGER)), allEvidence,
                new MechanicContext(new SpellTargetSubject(TargetScope.SINGLE_TARGET_UNSPECIFIED_CATEGORY),
                        List.of(new AppliesOnAttackRollOutcome(attackReference, AttackRollOutcome.HIT,
                                List.of(hit.id())))), MechanicsCompleteness.COMPLETE_WITHIN_CLAIM);

        List<UnsupportedMechanicsConstruct> unsupported = List.of(
                new UnsupportedMechanicsConstruct("magic-stone-unsupported:sling-range", MechanicFamily.ATTACK_ROLL,
                        UnsupportedReason.SOURCE_UNSPECIFIED, List.of(deliveryText.id()), List.of()));
        return MechanicsDocument.create(source.owner(), 1, List.of(source.source().revision()),
                new MechanicsProcessing(ProcessingState.PROCESSED, "xge-magic-stone", "1"),
                List.of(objects, attack, damageRecord), original.observations(), unsupported, allEvidence,
                new MechanicsCoverage(CoverageStatus.PARTIAL, "xge-magic-stone", "1",
                        List.of(root + "/time", root + "/range", root + "/duration", root + "/spellAttack",
                                root + "/damageInflict", root + "/entries/0", root + "/entries/1"),
                        List.of(), unsupported.size(), CoverageScopeExtent.DECLARED_SUBSET));
    }

    private static MechanicRecord claim(
            SpellMechanicsEvidenceSource source, MechanicId id, String slot, String pattern,
            com.thedndpub.data.mechanics.model.MechanicPayload payload,
            List<MechanicEvidenceAssociation> associations,
            List<com.thedndpub.data.mechanics.model.MechanicsEvidence> evidence,
            MechanicContext context, MechanicsCompleteness completeness) {
        var derivation = new com.thedndpub.data.mechanics.model.MechanicDerivation(
                DerivationMethod.BOUNDED_CONTEXT_INTERPRETATION, pattern, "1", "xge-magic-stone-1",
                List.of(), List.of(source.source().revision()));
        var assessment = new MechanicsAssessment(completeness, MechanicsSupport.BOUNDED_CONTEXT, List.of(), List.of());
        return MechanicRecord.propose(id, payload, context, associations, derivation, assessment);
    }

    private static MechanicEvidenceAssociation association(
            com.thedndpub.data.mechanics.model.MechanicsEvidence evidence, String path, EvidenceRole role) {
        return MechanicEvidenceAssociation.of(evidence, path, role);
    }
}
